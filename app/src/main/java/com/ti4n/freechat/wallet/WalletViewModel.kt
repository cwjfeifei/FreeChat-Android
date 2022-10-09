package com.ti4n.freechat.wallet

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.di.PreferencesDataStore
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.erc20.ERC20Tokens
import com.ti4n.freechat.erc20.ethereum
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
    val erC20Token: ERC20Tokens,
    @ApplicationContext context: Context
) :
    ViewModel() {

    val list = MutableStateFlow(erC20Token.result.map { it to 0.0 })
    val address = MutableStateFlow("")
    val account = context.dataStore.data.map { it[stringPreferencesKey("account")] ?: "" }

    init {
        viewModelScope.launch {
            account.filterNotNull().stateIn(this).collectLatest {
                Log.e("account", ": $it")
                address.value = MnemonicWords(it).address().hex
                getBalance(MnemonicWords(it))
                getEthBalance(address.value)
            }
        }
    }

    suspend fun getBalance(accountWords: MnemonicWords) {
        erC20Token.result.forEach { erc20 ->
            withContext(Dispatchers.IO) {
                try {
                    EthUtil.balanceOf(erc20.contractAddress, accountWords).collectLatest {
                        val wei = it.toWei(erc20.Decimals)
                        val new = erc20 to wei
                        Log.e("balance", "getBalance: ${erc20.Name}  $wei")
                        list.value.toMutableList().replaceAll { if (it.first == erc20) new else it }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    suspend fun getEthBalance(address: String) {
        withContext(Dispatchers.IO) {
            try {
                EthUtil.balanceOf(address).collectLatest {
                    val wei = it.balance.toWei(18)
                    list.value = list.value + (ethereum to wei)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}