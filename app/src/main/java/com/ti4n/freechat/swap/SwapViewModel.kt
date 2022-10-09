package com.ti4n.freechat.swap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.model.response.swap.SupportToken
import com.ti4n.freechat.network.SwapApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwapViewModel @Inject constructor(val swapApiService: SwapApiService) : ViewModel() {

    val supportTokens = MutableStateFlow(emptyList<SupportToken>())
    val fromToken = MutableStateFlow<SupportToken?>(null)
    val toToken = MutableStateFlow<SupportToken?>(null)
    val amount = MutableStateFlow("")

    init {
        viewModelScope.launch {
            try {
                val tokens = swapApiService.getSupportTokens().tokens.values.toList()
                supportTokens.value = tokens
                fromToken.value = tokens.firstOrNull()
                toToken.value = tokens[1]
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setFromToken(from: SupportToken) {
        fromToken.value = from
    }

    fun setToToken(to: SupportToken) {
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
                        fromToken.value!!.address,
                        toToken.value!!.address,
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
                        fromToken.value!!.address,
                        toToken.value!!.address,
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