package com.ti4n.freechat.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.db.UserBaseInfo
import com.ti4n.freechat.model.request.Login
import com.ti4n.freechat.model.request.Register
import com.ti4n.freechat.model.request.SendVerifyCode
import com.ti4n.freechat.model.response.freechat.FaceImageInfo
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.toast
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.util.IM.DEFAULT_FACEURL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RegisterViewModel"

@HiltViewModel
class RegisterViewModel @Inject constructor(
    val imService: FreeChatIMService,
    val freeChatApiService: FreeChatApiService,
    val db: AppDataBase
) : ViewModel() {

    val setEmailRoute = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)

    // wallet
    val words = MutableStateFlow(EthUtil.getMnemonicCode().words)
    val shuffledWord = MutableStateFlow(emptyList<String>())
    val clickedWords = MutableStateFlow(emptyList<String>())

    // For set Self UserInfo
    val faceURL = MutableStateFlow("")
    val name = MutableStateFlow("")
    val birth = MutableStateFlow(0L)
    val gender = MutableStateFlow(0)  // 1 male 2 female
    val email = MutableStateFlow("")

    val faceUrls = MutableStateFlow(emptyList<FaceImageInfo>())

    init {
        viewModelScope.launch {
            faceUrls.value = freeChatApiService.getAvatars().data ?: emptyList()
            faceURL.value = freeChatApiService.getAvatars().data?.firstOrNull()?.small ?: ""
        }
    }

    fun setFaceURL(faceURL: String) {
        this.faceURL.value = faceURL
    }

    fun setName(name: String) {
        this.name.value = name
    }

    fun setGender(gender: Int) {
        this.gender.value = gender
    }

    fun setEmail(email: String) {
        this.email.value = email
    }

    fun addWord(word: String) {
        clickedWords.value = clickedWords.value + word
    }

    fun deleteWord(word: String) {
        clickedWords.value = clickedWords.value - word
    }

    fun shuffleWord() {
        shuffledWord.value = words.value.shuffled()
    }

    fun resetWord() {
        clickedWords.value = emptyList()
    }

    fun canRegister() = words.value == clickedWords.value

    fun sendVerifyCode(userId: String) {
        if (email.value.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    if (imService.sendVerifyCode(
                            SendVerifyCode(
                                1, userId, email.value
                            )
                        ).errCode == 0
                    ) {
                        setEmailRoute.emit(Route.VerifyEmailRegister.jump(userId, email.value))
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun registerFreeChat(userID: String, verifyCode: String) {
        viewModelScope.launch {
            try {
                val response = imService.register(
                    Register(
                        userID = userID,
                        email = email.value,
                        verificationCode = verifyCode
                    )
                )
                if (response.errCode == 0 && response.data != null) {
                    db.userBaseInfoDao().insert(
                        UserBaseInfo(
                            userID = response.data.userID,
                            token = response.data.token,
                            email = email.value,
                            expiredTime = response.data.expiredTime
                        )
                    )

                    IM.logout()
                    IM.login(userID, response.data.token)
                    setEmailRoute.emit(Route.CompleteProfile.route)
                } else {
                    toast.emit(R.string.set_email_failed)
                }
            } catch (e: Exception) {
                // network error or userID registered
                toast.emit(R.string.set_email_failed)
                Log.w(TAG, "registerFreeChat: ", e)
            }
        }
    }


    fun login(address: String, verifyCode: String) {
        viewModelScope.launch {
            try {
                val response = imService.login(Login(address, verifyCode))
                if (response.errCode == 0) {
                    response.data?.let {
                        db.userBaseInfoDao().insert(
                            UserBaseInfo(
                                userID = address,
                                token = it.token,
                                expiredTime = it.expiredTime
                            )
                        )
                        IM.logout()
                        IM.login(address, it.token)
                    }
                    setEmailRoute.emit(Route.Home.route)
                } else {
                    toast.emit(R.string.wrong_password)
                }
            } catch (e: Exception) {
                Log.w("Login", "Error :  ", e)
            }
        }
    }
}