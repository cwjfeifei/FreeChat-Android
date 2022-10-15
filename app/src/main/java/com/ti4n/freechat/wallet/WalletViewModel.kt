package com.ti4n.freechat.wallet

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ti4n.freechat.di.PreferencesDataStore
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.erc20.ERC20Tokens
import com.ti4n.freechat.erc20.ethereum
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.paging.EthTransactionPagingSourceFactory
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.address
import com.ti4n.freechat.util.toWei
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.kethereum.bip39.model.MnemonicWords
import org.web3j.crypto.WalletUtils
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    val pagingSourceFactory: EthTransactionPagingSourceFactory,
    val freeChatApiService: FreeChatApiService,
    @ApplicationContext context: Context
) :
    ViewModel() {

    val list = MutableStateFlow<List<Pair<ERC20Token, String>>>(emptyList())
    val address = MutableStateFlow("")
    val account = context.dataStore.data.map { it[stringPreferencesKey("address")] ?: "" }
    val erc20Tokens = MutableStateFlow<List<ERC20Token>>(emptyList())
    val selectedToken = MutableStateFlow<ERC20Token?>(null)

    init {
        viewModelScope.launch {
            address.value = account.filterNotNull().first()
            erc20Tokens.value = freeChatApiService.getSupportTokens().result
            list.value = erc20Tokens.value.map { it to "0" }
            getBalance()
            getEthBalance(address.value)
        }
    }

    suspend fun getBalance() {
        erc20Tokens.value.forEach { erc20 ->
            try {
                val it = EthUtil.balanceOf(erc20, address.value)
                if (erc20.symbol == "USDT") {
                    Log.e("USDT", "getBalance: $it")
                }
                val new = erc20 to (it ?: "")
                val newList = list.value.toMutableList()
                newList.replaceAll { if (it.first == erc20) new else it }
                list.value = newList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getEthBalance(address: String) {
        try {
            val balance = EthUtil.balanceOf(address)
            list.value = list.value + (ethereum to balance.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setSelectedToken(token: ERC20Token) {
        selectedToken.value = token
    }

    fun createPager() = Pager(PagingConfig(20)) {
        pagingSourceFactory.create(address.value, selectedToken.value?.contractAddress)
    }.flow.cachedIn(viewModelScope)
}