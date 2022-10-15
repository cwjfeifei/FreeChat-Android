package com.ti4n.freechat.swap

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.erc20.ethereum
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.network.SwapApiService
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.toWeb3Credentials
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.kethereum.model.Address
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.crypto.WalletUtils
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigInteger
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class SwapViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val swapApiService: SwapApiService,
    val freeChatApiService: FreeChatApiService
) : ViewModel() {

    val supportTokens = MutableStateFlow(emptyList<ERC20Token>())
    val fromToken = MutableStateFlow<ERC20Token?>(null)
    val toToken = MutableStateFlow<ERC20Token?>(null)
    val amount = MutableStateFlow("")
    val fromAddress = MutableStateFlow("")
    val toAmount = MutableStateFlow("")
    val rate = MutableStateFlow("")
    val gas = MutableStateFlow(0)

    init {
        viewModelScope.launch {
            try {
                val tokens = listOf(ethereum) + freeChatApiService.getSupportTokens().result
                supportTokens.value = tokens
                fromToken.value = tokens.firstOrNull()
                toToken.value = tokens[1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        viewModelScope.launch {
            fromAddress.value = context.dataStore.data.map { it[stringPreferencesKey("address")] }
                .filter { !it.isNullOrEmpty() }.first() ?: ""
        }
    }

    fun setFromToken(from: ERC20Token) {
        fromToken.value = from
        getRate()
    }

    fun setToToken(to: ERC20Token) {
        toToken.value = to
        getRate()
    }

    fun setAmount(value: String) {
        amount.value = value
    }

    fun quote() {
        if (fromToken.value != null && toToken.value != null) {
            viewModelScope.launch {
                try {
                    val quote = swapApiService.quote(
                        fromToken.value!!.Address,
                        toToken.value!!.Address,
                        amount.value
                    )
                    gas.value = quote.estimatedGas
                    toAmount.value = quote.toTokenAmount
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getRate() {
        viewModelScope.launch {
            rate.value =
                freeChatApiService.getRate("${fromToken.value!!.symbol}-${toToken.value!!.symbol}").data.first().idxPx
        }
    }

    fun swap(password: String) {
        if (fromToken.value != null && toToken.value != null) {
            viewModelScope.launch {
                try {
                    val swap = swapApiService.swap(
                        fromToken.value!!.Address,
                        toToken.value!!.Address,
                        (0.00001 * (10.0.pow(fromToken.value!!.Decimals))).toBigDecimal()
                            .toPlainString(),
                        fromAddress.value,
                        1.0
                    )
                    val tx = swap.tx
                    val count = EthUtil.transactionCount(fromAddress.value)
                    val credentials = EthUtil.loadCredentials(context, "")
                    Log.e(
                        "Transaction",
                        "fromAmount: ${
                            credentials?.address
                        }"
                    )
                    Log.e(
                        "Transaction",
                        "gas: ${
                            ((tx.gas.toBigDecimal() * tx.gasPrice.toBigDecimal() + swap.fromTokenAmount.toBigDecimal()).toDouble() / (10.0.pow(
                                18
                            ))).toBigDecimal().toPlainString()
                        }"
                    )
                    EthUtil.web3.ethSendRawTransaction(
                        Numeric.toHexString(
                            TransactionEncoder.signMessage(
                                RawTransaction.createTransaction(
                                    count,
                                    BigInteger(tx.gasPrice),
                                    BigInteger(tx.gas),
                                    tx.to,
                                    BigInteger(tx.value),
                                    tx.data,
                                ), credentials
                            )
                        )
                    ).flowable().doOnError { it.printStackTrace() }.asFlow()
                        .catch { it.printStackTrace() }.flowOn(Dispatchers.IO).collectLatest {
                            Log.e("Transaction", "swap: ${it.transactionHash}", )
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}