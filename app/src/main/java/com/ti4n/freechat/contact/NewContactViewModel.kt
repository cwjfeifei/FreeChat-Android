package com.ti4n.freechat.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.util.IM
import dagger.hilt.android.lifecycle.HiltViewModel
import io.openim.android.sdk.models.FriendApplicationInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewContactViewModel @Inject constructor() : ViewModel() {

    val friendApplications = MutableStateFlow(emptyList<FriendApplicationInfo>())

    init {
        viewModelScope.launch {
            IM.getFriendApplication()?.let {
                friendApplications.value = it
            }
        }
    }
}