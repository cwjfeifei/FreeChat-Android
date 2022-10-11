package com.ti4n.freechat.wallet

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.erc20.ERC20Tokens
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.address
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
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
    val tokens: ERC20Tokens,
    @ApplicationContext val context: Context
) : ViewModel() {

    val toAddress = MutableStateFlow("")
    val amount = MutableStateFlow("")
    val selectedToken = MutableStateFlow(tokens.result.first())
    val gas = MutableStateFlow("")

    fun setAddress(address: String) {
        toAddress.value = address
        viewModelScope.launch(Dispatchers.IO) {
            EthUtil.gasPrice(
                "0x43b083475fd9bc41df86263af2e2badd688697e1",
                "0x43b083475fd9bc41df86263af2e2badd688697e1"
            )
                .collectLatest {
                    Log.e("GAS", "setAddress: $it")
                }
        }
    }

    fun setAmount(_amount: String) {
        amount.value = _amount
    }

    fun setSelectedToken(token: ERC20Token) {
        selectedToken.value = token
    }

    fun transfer() {
        viewModelScope.launch(Dispatchers.IO) {
            EthUtil.transferERC20(
                context,
                toAddress.value,
                amount.value,
                selectedToken.value.contractAddress,
                selectedToken.value.Decimals,
                "",
                context.dataStore.data.map { it[stringPreferencesKey("file")] }
                    .filter { !it.isNullOrEmpty() }.first() ?: ""
            ).collectLatest {

            }
        }
    }
}