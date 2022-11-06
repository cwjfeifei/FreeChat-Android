package com.ti4n.freechat.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.util.IM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PickFaceImageViewModel @Inject constructor(
    val imService: FreeChatIMService,
    val db: AppDataBase
) : ViewModel() {
    private val TAG = "RegisterViewModel"

    val faceData = MutableStateFlow(emptyList<String>())
    val gender = MutableStateFlow(2)  // 1 male 2 female
    val faceURL = MutableStateFlow("https://freechat.world/images/face.apng")

    init {
        viewModelScope.launch {
            var selfInfo = IM.currentUserInfo.value
            if (selfInfo.userID != null) {
                var dbInfo = db.userBaseInfoDao().getUserInfo(selfInfo.userID).firstOrNull()
                if (dbInfo != null) {
                    faceURL.value = if (dbInfo.faceURL.isEmpty()) {
                        "https://freechat.world/images/face.apng"
                    } else {
                        dbInfo.faceURL
                    }
                }
            }
        }
    }

    fun setGender(gender: Int) {
        this.gender.value = gender
    }
}