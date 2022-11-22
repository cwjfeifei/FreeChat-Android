package com.ti4n.freechat.wallet

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.model.response.freechat.ERC20Token
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.paging.EthTransactionPagingSourceFactory
import com.ti4n.freechat.util.EthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    val pagingSourceFactory: EthTransactionPagingSourceFactory,
    val freeChatApiService: FreeChatApiService,
    @ApplicationContext context: Context
) :
    ViewModel() {

    val list = mutableStateListOf<TokenValue>()
    val address = MutableStateFlow("")
    val account = context.dataStore.data.map { it[stringPreferencesKey("address")] ?: "" }
    val erc20Tokens = MutableStateFlow<List<ERC20Token>>(emptyList())
    val selectedToken = MutableStateFlow<ERC20Token?>(null)

    init {
        viewModelScope.launch {
            address.value = account.filterNotNull().first()
            erc20Tokens.value = freeChatApiService.getSupportTokens().result
            list.addAll(erc20Tokens.value.map { TokenValue(it, "0", "0") })
            getBalance()
        }
    }

    suspend fun getBalance() {
        erc20Tokens.value.forEach { erc20 ->
            try {
                if (erc20.symbol == "ETH") {
                    val balance = EthUtil.balanceOf(address.value) ?: "0"
                    val rate = freeChatApiService.getRate("ETH-USD").data.firstOrNull()?.idxPx
                    list.replaceAll {
                        if (it.token == erc20) it.copy(
                            balance = balance,
                            usd = ((balance.toDoubleOrNull() ?: 0.0) * (rate?.toDoubleOrNull()
                                ?: 0.0)).toString()
                        ) else it
                    }
                } else {
                    val tokenBalance = EthUtil.balanceOf(erc20, address.value) ?: "0"
                    val rate =
                        freeChatApiService.getRate("${erc20.symbol}-USD").data.firstOrNull()?.idxPx
                    list.replaceAll {
                        if (it.token == erc20) it.copy(
                            balance = tokenBalance,
                            usd = ((tokenBalance.toDoubleOrNull() ?: 0.0) * (rate?.toDoubleOrNull()
                                ?: 0.0)).toString()
                        ) else it
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun setSelectedToken(token: ERC20Token) {
        selectedToken.value = token
    }
}

data class TokenValue(val token: ERC20Token, var balance: String, var usd: String)