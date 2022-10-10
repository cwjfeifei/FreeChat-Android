package com.ti4n.freechat.swap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.network.SwapApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwapViewModel @Inject constructor(val swapApiService: SwapApiService) : ViewModel() {

    val supportTokens = MutableStateFlow(emptyList<ERC20Token>())
    val fromToken = MutableStateFlow<ERC20Token?>(null)
    val toToken = MutableStateFlow<ERC20Token?>(null)
    val amount = MutableStateFlow("")

    init {
        viewModelScope.launch {
            try {
                val tokens = swapApiService.getSupportTokens().result
                supportTokens.value = tokens
                fromToken.value = tokens.firstOrNull()
                toToken.value = tokens[1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setFromToken(from: ERC20Token) {
        fromToken.value = from
    }

    fun setToToken(to: ERC20Token) {
        toToken.value = to
    }

    fun setAmount(value: String) {
        amount.value = value
    }

    fun quote() {
        if (fromToken.value != null && toToken.value != null) {
            viewModelScope.launch {
                try {
                    swapApiService.quote(
                        fromToken.value!!.Address,
                        toToken.value!!.Address,
                        amount.value
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun swap() {
        if (fromToken.value != null && toToken.value != null) {
            viewModelScope.launch {
                try {
                    swapApiService.swap(
                        fromToken.value!!.Address,
                        toToken.value!!.Address,
                        amount.value,
                        "",
                        0.5
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}