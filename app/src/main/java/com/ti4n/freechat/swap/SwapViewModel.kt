package com.ti4n.freechat.swap

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.erc20.ethereum
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.network.SwapApiService
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.toWei
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.asFlow
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.utils.Numeric
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
    val fromUSD = MutableStateFlow("")
    val fromAddress = MutableStateFlow("")
    val toAmount = MutableStateFlow("")
    val toUSD = MutableStateFlow("")
    val rate = MutableStateFlow("")
    val gas = MutableStateFlow(0)
    val gasUSD = MutableStateFlow("")

    val fromBalance = MutableStateFlow("")
    val toBalance = MutableStateFlow("")

    val quoteAmount = MutableStateFlow("")

    val transactionSend = MutableSharedFlow<Boolean>(0, 1)
    val transactionHash = MutableStateFlow("")

    val impact = MutableStateFlow("")

    init {
        viewModelScope.launch {
            try {
                val tokens = listOf(ethereum) + freeChatApiService.getSupportTokens().result
                supportTokens.value = tokens
                fromToken.value = tokens.firstOrNull()
                toToken.value = tokens[1]
                setFromToken(fromToken.value!!)
                setToToken(toToken.value!!)
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
        viewModelScope.launch {
            fromUSD.value = getRate(from.symbol)
            calculateRate()
        }
        getFromBalance()
    }

    fun setToToken(to: ERC20Token) {
        toToken.value = to
        viewModelScope.launch {
            toUSD.value = getRate(to.symbol)
            calculateRate()
        }
        getToBalance()
    }

    fun calculateRate() {
        if (fromUSD.value.isNotEmpty() && toUSD.value.isNotEmpty()) {
            rate.value =
                (fromUSD.value.toDouble() / toUSD.value.toDouble()).toBigDecimal().toPlainString()
        }
    }

    fun setAmount(value: String) {
        amount.value = value
        toAmount.value =
            (amount.value.toDouble() * fromUSD.value.toDouble() / toUSD.value.toDouble()).toBigDecimal()
                .toPlainString()
        quote()
    }

    fun setToAmount(value: String) {
        toAmount.value = value
        amount.value =
            (toAmount.value.toDouble() * fromUSD.value.toDouble() / toUSD.value.toDouble()).toBigDecimal()
                .toPlainString()
        quote()
    }

    fun quote() {
        if (fromToken.value != null && toToken.value != null) {
            viewModelScope.launch {
                try {
                    val quote = swapApiService.quote(
                        fromToken.value!!.Address,
                        toToken.value!!.Address,
                        ((amount.value.toDoubleOrNull()
                            ?: 0.0) * 10.0.pow(fromToken.value!!.Decimals)).toBigDecimal()
                            .toPlainString()
                    )
                    gasUSD.value =
                        ((EthUtil.gasPrice()?.toFloat() ?: 0F) * quote.estimatedGas.toFloat() *
                                (freeChatApiService.getRate("ETH-USD").data.firstOrNull()?.idxPx
                                    ?: "1").toFloat()).toWei(18)
                    quoteAmount.value = (quote.toTokenAmount.toDoubleOrNull()
                        ?: 0.0).toWei(toToken.value!!.Decimals)
                    impact.value =
                        ((toAmount.value.toDouble() - quoteAmount.value.toDouble()) * 100.0 / toAmount.value.toDouble()).toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun getRate(symbol: String): String {
        return freeChatApiService.getRate("$symbol-USD").data.firstOrNull()?.idxPx
            ?: "1"
    }

    fun swap(password: String) {
        if (fromToken.value != null && toToken.value != null) {
            viewModelScope.launch {
                try {
                    val swap = swapApiService.swap(
                        fromToken.value!!.Address,
                        toToken.value!!.Address,
                        ((amount.value.toDoubleOrNull()
                            ?: 0.0) * (10.0.pow(fromToken.value!!.Decimals))).toBigDecimal()
                            .toPlainString(),
                        fromAddress.value,
                        1.0
                    )
                    val tx = swap.tx
                    val count = EthUtil.transactionCount(fromAddress.value)
                    val credentials = EthUtil.loadCredentials(context, password)
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
                            transactionHash.value = it.transactionHash
                            transactionSend.tryEmit(true)
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getFromBalance() {
        fromToken.value?.let {
            viewModelScope.launch {
                fromBalance.value = if (it.symbol == "ETH") EthUtil.balanceOf(fromAddress.value)
                    ?: "0" else EthUtil.balanceOf(it, fromAddress.value) ?: "0"
            }
        }
    }

    fun getToBalance() {
        toToken.value?.let {
            viewModelScope.launch {
                toBalance.value = EthUtil.balanceOf(it, fromAddress.value) ?: "0"
            }
        }
    }
}