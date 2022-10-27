package com.ti4n.freechat.home

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.db.UserBaseInfo
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.wethereum
import com.ti4n.freechat.wallet.TokenValue
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MeViewModel @Inject constructor(val db: AppDataBase, @ApplicationContext context: Context) :
    ViewModel() {

    private val account = context.dataStore.data.map { it[stringPreferencesKey("address")] ?: "" }
    val address = MutableStateFlow("")
    val me = MutableStateFlow<UserBaseInfo?>(null)

    init {
        viewModelScope.launch {
            address.value = account.filterNotNull().first()
            db.userBaseInfoDao().getUserInfo(address.value).collectLatest {
                me.value = it
            }
        }
    }
}