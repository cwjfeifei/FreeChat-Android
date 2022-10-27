package com.ti4n.freechat.login

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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
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
                val response = imService.getInfo(GetSelfInfo(it.userID), it.token)
                if (response.errCode == 0 && response.data != null) {
                    val selfInfo = response.data
                    db.userBaseInfoDao().insert(
                        UserBaseInfo(
                            selfInfo.userID,
                            selfInfo.nickname,
                            token.token,
                            selfInfo.faceURL,
                            selfInfo.birth
                        )
                    )
                    navigationRoute.emit(Route.Home.route)
                } else {
                    navigationRoute.emit(Route.CompleteProfile.route)
                }
            }
        }
    }
}