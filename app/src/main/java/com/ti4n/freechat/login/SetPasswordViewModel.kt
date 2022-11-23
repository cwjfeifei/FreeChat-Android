package com.ti4n.freechat.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.Route
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.model.request.SendVerifyCode
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.toast
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.IM
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SetPasswordViewModel XXX"

@HiltViewModel
class SetPasswordViewModel @Inject constructor(
    val imService: FreeChatIMService,
    val db: AppDataBase,
) : ViewModel() {

    val navigationRoute = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)

    // set wallet password
    fun onSetPassword(address: String) {
        viewModelScope.launch {
            try {
                val response = imService.sendVerifyCode(SendVerifyCode(3, address, ""))
                if ((response.errCode == 0 || response.errCode == 10006)) {
                    navigationRoute.emit(
                        Route.VerifyEmailLogin.jump(
                            address,
                            response.data?.account ?: "Email"
                        )
                    )
                } else if (response.errCode == 10003) {
                    navigationRoute.emit(Route.SetEmail.jump(address))
                } else {

                }
            } catch (e: Exception) {
                Log.w(TAG, "onSetPassword Error : ", e)
            }
        }
    }

}