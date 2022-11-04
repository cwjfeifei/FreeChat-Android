package com.ti4n.freechat.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.Route
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.db.UserBaseInfo
import com.ti4n.freechat.model.request.GetSelfInfo
import com.ti4n.freechat.model.request.GetToken
import com.ti4n.freechat.network.FreeChatIMService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetPasswordViewModel @Inject constructor(
    val imService: FreeChatIMService,
    val db: AppDataBase
) : ViewModel() {

    val navigationRoute = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)

    fun login(address: String) {
        viewModelScope.launch {
            val token = imService.getToken(GetToken(address)).data
            token?.let {
                val response = imService.getSelfInfo(GetSelfInfo(it.userID), it.token)
                if (response.errCode == 0 && response.data != null) {
                    val selfInfo = response.data
//                    "data": {
//                        "appMangerLevel": 1,
//                        "birth": 1640692941,
//                        "email": "6e7123@qq.com",
//                        "faceURL": "https://oss.com.cn/head",
//                        "gender": 1,
//                        "nickname": "dk111223",
//                        "userID": "kh12312"
//                    }
                    db.userBaseInfoDao().insert(
                        UserBaseInfo(
                            userID = address,
                            nickname = selfInfo.nickname,
                            faceURL = selfInfo.faceURL,
                            birth = selfInfo.birth,
                            gender = selfInfo.gender,
                            email = selfInfo.email,
                            token =   token.token,
                            expiredTime = token.expiredTime
                        )
                    )
                    navigationRoute.emit(Route.Home.route)
                } else {
                    navigationRoute.emit(Route.SetEmail.route)
                }
            }
        }
    }

}