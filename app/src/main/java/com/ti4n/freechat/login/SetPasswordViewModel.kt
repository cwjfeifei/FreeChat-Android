package com.ti4n.freechat.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.Route
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.db.UserBaseInfo
import com.ti4n.freechat.model.request.GetSelfInfo
import com.ti4n.freechat.model.request.GetToken
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.util.IM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "SetPasswordViewModel XXX"

@HiltViewModel
class SetPasswordViewModel @Inject constructor(
    val imService: FreeChatIMService,
    val db: AppDataBase
) : ViewModel() {

    val navigationRoute = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)

    // set wallet password
    fun onSetPassword(address: String) {
        viewModelScope.launch {
            try {
                val token = imService.getToken(GetToken(address)).data
                token?.let {
                    val response = imService.getSelfInfo(GetSelfInfo(it.userID), it.token)
                    Log.w(TAG, "Freechat account: " + address + " resp: " + response)
                    if (response.errCode == 0 && response.data != null) {
                        val selfInfo = response.data
                        db.userBaseInfoDao().insert(
                            UserBaseInfo(
                                userID = address,
                                nickname = selfInfo.nickname,
                                faceURL = selfInfo.faceURL,
                                birth = selfInfo.birth,
                                gender = selfInfo.gender,
                                email = selfInfo.email ?: "",
                                token = token.token,
                                expiredTime = token.expiredTime
                            )
                        )
                        if (selfInfo.faceURL == "") {
                            // registered but not set faceURL
                            navigationRoute.emit(Route.CompleteProfile.route)
                        } else {
                            navigationRoute.emit(Route.Home.route)
                            IM.login(address, token.token)
                        }
                    } else {
                        navigationRoute.emit(Route.SetEmail.jump(address))
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "onSetPassword Error : ", e)
            }
        }
    }

}