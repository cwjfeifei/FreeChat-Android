package com.ti4n.freechat.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.db.UserBaseInfo
import com.ti4n.freechat.util.IM
import dagger.hilt.android.lifecycle.HiltViewModel
import io.openim.android.sdk.models.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    val db: AppDataBase
) : ViewModel() {

    val toUserId = savedStateHandle.get<String>("id") ?: ""
    val isFriend = MutableStateFlow(false)
    val isSelf = MutableStateFlow(false)
    val userInfo = MutableStateFlow(UserInfo())
    val selfInfo = MutableStateFlow(UserBaseInfo("", expiredTime = 0L, token = ""))

    init {
        viewModelScope.launch {
            isFriend.value = IM.isFriend(toUserId)
        }
        viewModelScope.launch {
            db.userBaseInfoDao().getUserInfo().filterNotNull().collectLatest {
                selfInfo.value = it
                isSelf.value = it.userID == toUserId
                if (isSelf.value) {
                    userInfo.value = UserInfo().apply {
                        birth = it.birth
                        userID = it.userID
                        faceURL = it.faceURL
                        nickname = it.nickname
                        remark = ""
                    }
                } else {
                    userInfo.value = IM.getUserInfo(toUserId)?.firstOrNull() ?: UserInfo()
                }
            }
        }
    }

    fun addFriend(requestInfo: String) {
        viewModelScope.launch {
            IM.addFriend(toUserId,requestInfo)
        }
    }

    fun setRemark(remark: String) {
        viewModelScope.launch {
            IM.setRemark(toUserId, remark)
        }
    }
}