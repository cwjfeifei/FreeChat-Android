package com.ti4n.freechat.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.Route
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.model.request.GetToken
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.IM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel XXX"

@HiltViewModel
class LoginViewModel @Inject constructor(
    val imService: FreeChatIMService,
    val db: AppDataBase
) : ViewModel() {

    val autoLoginRoute = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)

    val word1 = mutableStateOf("")
    val word2 = mutableStateOf("")
    val word3 = mutableStateOf("")
    val word4 = mutableStateOf("")
    val word5 = mutableStateOf("")
    val word6 = mutableStateOf("")
    val word7 = mutableStateOf("")
    val word8 = mutableStateOf("")
    val word9 = mutableStateOf("")
    val word10 = mutableStateOf("")
    val word11 = mutableStateOf("")
    val word12 = mutableStateOf("")

    fun wordsIsCorrect() =
        EthUtil.mnemonicWordsExist("${word1.value} ${word2.value} ${word3.value} ${word4.value} ${word5.value} ${word6.value} ${word7.value} ${word8.value} ${word9.value} ${word10.value} ${word11.value} ${word12.value}")


    // auto check words and token, do login
    fun autoLogin(context: Context) {
        viewModelScope.launch {
            val address =
                context.dataStore.data.map { it[stringPreferencesKey("address")] }.firstOrNull()

            if (address == null) {
                // go to Login : login or register
                autoLoginRoute.emit(Route.MainLogin.route)
                cancel()
            } else {
                var dbUserInfo = db.userBaseInfoDao().getUserInfo(address).firstOrNull()

                if (dbUserInfo == null) { // No Freechat login user Log.d(TAG, "autoLogin: no address ")
                    autoLoginRoute.emit(Route.MainLogin.route)
                } else {
                    val OneDayDuration = 7 * 24 * 60 * 60 // unit: s
                    val toExpiredDuration =
                        dbUserInfo.expiredTime - System.currentTimeMillis() / 1000
                    // almost 90 days has not login -> go to login
                    var tokenExpired =
                        dbUserInfo.token == null || toExpiredDuration < OneDayDuration
                    if (tokenExpired) {
                        autoLoginRoute.emit(Route.MainLogin.route)
                    } else {
                        IM.logout()
                        IM.login(address, dbUserInfo.token)
                        autoLoginRoute.emit(Route.Home.route)

                        // 自动登录时刷新token 时间
                        val OneWeekDuration = 7 * OneDayDuration
                        if (toExpiredDuration < OneWeekDuration) {
                            try {
                                val token = imService.getToken(GetToken(address)).data
                                token?.let {
                                    dbUserInfo.token = token.token
                                    dbUserInfo.expiredTime = token.expiredTime

                                    db.userBaseInfoDao().insert(dbUserInfo)
                                }
                            } catch (e: java.lang.Exception) {
                                Log.w(TAG, "update token Exception: ", e)
                            }
                        }
                    }
                }
            }
        }
    }
}