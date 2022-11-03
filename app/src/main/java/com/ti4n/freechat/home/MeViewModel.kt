package com.ti4n.freechat.home

import android.content.Context
import android.util.Log
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
import io.openim.android.sdk.OpenIMClient
import io.openim.android.sdk.listener.OnBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MeViewModel"
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


    /**
     * @param nickname    名字
     * @param faceURL     头像
     * @param gender      1 male, 2 female
     * @param birth       出生日期
     * @param email       邮箱
     * @param base        callback String
     */
    fun setSelfInfo(
        nickname: String?, faceURL: String?, gender: Int,
        birth: Long, email: String?, ex: String?
    ) {
        val callBack: OnBase<String> = object : OnBase<String> {
            override fun onError(code: Int, error: String) {
                Log.w(TAG, "setSelfInfo onError: " + error +" " + code )
            }

            override fun onSuccess(data: String?) {
            }
        }
        viewModelScope.launch {
            // 	appMangerLevel { AppOrdinaryUsers = 1, AppAdmin = 2}
            OpenIMClient.getInstance().userInfoManager.setSelfInfo(
                callBack,
                nickname,
                faceURL,
                gender,
                1,
                "",
                birth,
                email,
                ex
            )
        }
    }
}