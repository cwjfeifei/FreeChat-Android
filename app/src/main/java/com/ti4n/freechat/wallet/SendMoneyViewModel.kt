package com.ti4n.freechat.wallet

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.erc20.ERC20Tokens
import com.ti4n.freechat.erc20.ethereum
import com.ti4n.freechat.erc20.wethereum
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.address
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.kethereum.bip39.model.MnemonicWords
import javax.inject.Inject

@HiltViewModel
class SendMoneyViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val freeChatApiService: FreeChatApiService
) : ViewModel() {
    val tokens = MutableStateFlow(emptyList<ERC20Token>())

    val toAddress = MutableStateFlow("")
    val amount = MutableStateFlow("")
    val selectedToken = MutableStateFlow<ERC20Token?>(null)
    val fromAddress = MutableStateFlow("")
    val remainAmount = MutableStateFlow("")

    val gas = MutableStateFlow("")
    val maxGas = MutableStateFlow("")
    val gasUSD = MutableStateFlow("")
    val maxGasUSD = MutableStateFlow("")

    val transactionSend = MutableSharedFlow<Boolean>(0, 1)

    init {
        viewModelScope.launch {
            fromAddress.value = context.dataStore.data.map { it[stringPreferencesKey("address")] }
                .filter { !it.isNullOrEmpty() }.first() ?: ""
        }
        viewModelScope.launch {
            tokens.value =
                listOf(ethereum, wethereum) + freeChatApiService.getSupportTokens().result
            setSelectedToken(tokens.value.first())
        }
    }

    fun setAddress(address: String) {
        toAddress.value = address
    }

    fun addressDone() {
        if (toAddress.value.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                EthUtil.gasPrice(
                    fromAddress.value,
                    toAddress.value
                )
                    .collectLatest {
                        it?.let {
                            maxGas.value = it.first
                            gas.value = it.second
                            val rate =
                                freeChatApiService.getRate("ETH-USD").data.first()
                            gasUSD.value =
                                (it.second.toDouble() * rate.idxPx.toDouble()).toBigDecimal()
                                    .toPlainString()
                            maxGasUSD.value =
                                (it.first.toDouble() * rate.idxPx.toDouble()).toBigDecimal()
                                    .toPlainString()
                            Log.e("USD", "addressDone: ${gasUSD.value}  ${maxGasUSD.value}")
                        }
                    }
            }
        }
    }

    fun setAmount(_amount: String) {
        amount.value = _amount
    }

    fun setSelectedToken(token: ERC20Token) {
        selectedToken.value = token
        viewModelScope.launch {
            remainAmount.value = EthUtil.balanceOf(
                token, fromAddress.value
            )?.toBigDecimal()?.toPlainString() ?: ""
        }

    }

    fun transfer() {
        viewModelScope.launch(Dispatchers.IO) {
            if (selectedToken.value?.symbol != "ETH")
                EthUtil.transferERC20(
                    context,
                    toAddress.value,
                    amount.value,
                    selectedToken.value!!.contractAddress,
                    selectedToken.value!!.Decimals,
                    "",
                ).collectLatest {
                    transactionSend.tryEmit(true)
                }
            else EthUtil.transfer(
                context,
                toAddress.value,
                amount.value,
                "",
            ).collectLatest {
                transactionSend.tryEmit(true)
            }
        }
    }
}