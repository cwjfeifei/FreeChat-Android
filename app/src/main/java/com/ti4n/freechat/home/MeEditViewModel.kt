package com.ti4n.freechat.home

import android.content.Context
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.model.response.freechat.FaceImageInfo
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.util.IM
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

private const val TAG = "MeViewModel"

@HiltViewModel
class MeEditViewModel @Inject constructor(
    val db: AppDataBase,
    val freeChatApiService: FreeChatApiService,
    @ApplicationContext context: Context
) :
    ViewModel() {

    // For set Self UserInfo
    val faceURL = MutableStateFlow(IM.DEFAULT_FACEURL)
    val nickname = MutableStateFlow("")
    val gender = MutableStateFlow(2)  // 1 male 2 female 3-跨性别 db
    val birth = MutableStateFlow<Long>(0)
    val email = MutableStateFlow("")

    val faceUrls = MutableStateFlow(emptyList<FaceImageInfo>())

    init {
        viewModelScope.launch {
            var selfInfo = IM.currentUserInfo.value
            selfInfo?.let {
                faceURL.value = if (it.faceURL == null) IM.DEFAULT_FACEURL else it.faceURL
                nickname.value = if (it.nickname == null) "" else it.nickname
                gender.value = it.gender
                birth.value = it.birth * 1000
                email.value = if (it.email == null) "" else it.email
            }
            faceUrls.value = freeChatApiService.getAvatars().data ?: emptyList()
        }
    }

    fun setFaceURL(faceURL: String) {
        this.faceURL.value = faceURL
    }

    fun setNickname(name: String) {
        this.nickname.value = name
    }

    fun setGender(gender: Int) {
        this.gender.value = gender
    }

    fun setEmail(email: String) {
        this.email.value = email
    }

    fun setBirth(year: Int, month: Int, day: Int) {
        val c = Calendar.getInstance()
        c.set(year, month - 1, day)
        this.birth.value = c.timeInMillis
    }
}