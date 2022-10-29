package com.ti4n.freechat.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.util.IM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor() : ViewModel() {

    fun pinConversation(conversationId: String, isPin: Boolean) {
        viewModelScope.launch {
            IM.pinConversation(conversationId, isPin)
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            IM.deleteConversation(conversationId)
        }
    }
}