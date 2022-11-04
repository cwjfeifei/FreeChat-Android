package com.ti4n.freechat.im

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.paging.MessagePagingSourceFactory
import com.ti4n.freechat.util.IM
import dagger.hilt.android.lifecycle.HiltViewModel
import io.openim.android.sdk.models.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivateChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    messagePagingSourceFactory: MessagePagingSourceFactory,
    db: AppDataBase
) :
    ViewModel() {
    val toUserId = savedStateHandle.get<String>("id") ?: ""
    val conversationId = savedStateHandle.get<String>("conversationId") ?: ""
    val messagePager = Pager(PagingConfig(20), null) {
        messagePagingSourceFactory.create(toUserId, conversationId)
    }.flow.cachedIn(viewModelScope)

    val toUserInfo = MutableStateFlow(UserInfo())
    val mineInfo =
        db.userBaseInfoDao().getUserInfo().stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        viewModelScope.launch {
            toUserInfo.value = IM.getUserInfo(toUserId)?.firstOrNull() ?: UserInfo()
        }
        IM.newMessages.clear()
    }
}