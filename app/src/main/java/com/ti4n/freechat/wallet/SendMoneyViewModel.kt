package com.ti4n.freechat.wallet

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.erc20.ERC20Tokens
import com.ti4n.freechat.util.EthUtil
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

    fun setAddress(address: String) {
        toAddress.value = address
    }

    fun setAmount(_amount: String) {
        amount.value = _amount
    }

    fun setSelectedToken(token: ERC20Token) {
        selectedToken.value = token
    }

    fun transfer() {
        viewModelScope.launch(Dispatchers.IO) {
            EthUtil.transfer(
                toAddress.value,
                amount.value,
                selectedToken.value.contractAddress,
                MnemonicWords(context.dataStore.data.map { it[stringPreferencesKey("account")] }
                    .filter { !it.isNullOrEmpty() }.first() ?: "")
            ).collectLatest {
            }
        }
    }
}