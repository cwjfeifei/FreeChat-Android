package com.ti4n.freechat.profile

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.db.UserBaseInfo
import com.ti4n.freechat.model.im.BaseInfo
import com.ti4n.freechat.model.im.IFriendInfo
import com.ti4n.freechat.model.im.toBaseInfo
import com.ti4n.freechat.util.IM
import dagger.hilt.android.lifecycle.HiltViewModel
import io.openim.android.sdk.models.FriendInfo
import io.openim.android.sdk.models.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val toUserId = savedStateHandle.get<String>("id") ?: ""
    val isFriend = MutableStateFlow(false)
    val isSelf = MutableStateFlow(false)
    val userInfo = MutableStateFlow<BaseInfo?>(null)

    val refuseSuccess = MutableStateFlow(false)
    val approveSuccess = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            isFriend.value = IM.isFriend(toUserId)
        }
        viewModelScope.launch {
            isSelf.value = IM.currentUserInfo.value?.userID == toUserId
            if (isSelf.value) {
                userInfo.value = IM.currentUserInfo.value
            } else {
                IM.friends.collectLatest {
                    val f = it.find { it.userID == toUserId }
                    if (f != null) {
                        userInfo.value = f
                    } else {
                        userInfo.value = IM.getUserInfo(toUserId)?.toBaseInfo()
                    }
                }
            }
        }
    }

    fun addFriend(requestInfo: String) {
        viewModelScope.launch {
            IM.addFriend(toUserId, requestInfo)
        }
    }

    fun setRemark(remark: String) {
        viewModelScope.launch {
            IM.setRemark(toUserId, remark)
        }
    }

    fun acceptFriendApplication() {
        viewModelScope.launch {
            IM.acceptFriendApplication(toUserId)
            approveSuccess.value = true
            isFriend.value = true
        }
    }

    fun refuseFriendApplication(message: String) {
        viewModelScope.launch {
            IM.rejectFriendApplication(toUserId, message)
            refuseSuccess.value = true
        }
    }
}