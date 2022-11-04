package com.ti4n.freechat.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.db.UserBaseInfo
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.model.request.GetSelfInfo
import com.ti4n.freechat.model.request.GetToken
import com.ti4n.freechat.model.request.Register
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.address
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.kethereum.bip39.model.MnemonicWords
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    val imService: FreeChatIMService,
    val db: AppDataBase
) : ViewModel() {
    private val TAG = "RegisterViewModel"

    val setEmailRoute = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    // wallet
    val words = MutableStateFlow(EthUtil.getMnemonicCode().words)
    val shuffledWord = MutableStateFlow(emptyList<String>())
    val clickedWords = MutableStateFlow(emptyList<String>())

    // FreeChat
    //    val avatar = MutableStateFlow<Uri?>(null)
    val faceURL = MutableStateFlow("")
    val name = MutableStateFlow("")
    val birth = MutableStateFlow(0L)
    val gender = MutableStateFlow(1)  // 1 male 2 female
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    fun setFaceURL(faceURL: String) {
        this.faceURL.value = faceURL
    }

    fun setName(name: String) {
        this.name.value = name
    }

    fun setBirthday(birthday: Long) {
        this.birth.value = birthday
    }

    fun setGender(gender: Int) {
        this.gender.value = gender
    }

    fun setEmail(email: String) {
        this.email.value = email
    }

    fun addWord(word: String) {
        clickedWords.value = clickedWords.value + word
        shuffledWord.value = shuffledWord.value - word
    }

    fun deleteWord(word: String) {
        clickedWords.value = clickedWords.value - word
        shuffledWord.value = shuffledWord.value + word
    }

    fun shuffleWord() {
        shuffledWord.value = words.value.shuffled() - clickedWords.value.toSet()
    }

    fun canRegister() = words.value == clickedWords.value

    fun registerFreeChat(
        context: Context,
        words: String,
        email: String,
    ) {
        var userID = MnemonicWords(words).address().hex // wallet address
        viewModelScope.launch {
            try {
                val response = imService.register(
                    Register(
                        userID = userID,
                        email = email,
                    )
                )
                if (response.errCode == 0 && response.data != null) {
                    context.dataStore.edit {
                        it[stringPreferencesKey("userId")] = response.data.userID
                        it[stringPreferencesKey("token")] = response.data.token
                        it[stringPreferencesKey("expiredTime")] = response.data.expiredTime.toString()
                    }
                    db.userBaseInfoDao().insert(
                        UserBaseInfo(
                            userID = response.data.userID,
                            token = response.data.token,
                            email = email,
                            expiredTime= response.data.expiredTime
                        )
                    )

                    setEmailRoute.emit(Route.CompleteProfile.route)
                } else {
                    Toast.makeText(context, R.string.set_email_failed, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // network error or userID registered
                Toast.makeText(context, R.string.set_email_failed, Toast.LENGTH_SHORT).show()
                Log.w(TAG, "registerFreeChat: ", e)
            }
        }
    }


    fun login(address: String) {
        viewModelScope.launch {
            try {
                val token = imService.getToken(GetToken(address)).data
                token?.let {
                    val response = imService.getSelfInfo(GetSelfInfo(it.userID), it.token)
                    Log.w("Login", "resp-getselfuserinfo " + response)
                    if (response.errCode == 0 && response.data != null) {
                        val selfInfo = response.data
                        db.userBaseInfoDao().insert(
                            UserBaseInfo(
                                userID = address,
                                nickname = selfInfo.nickname,
                                faceURL = selfInfo.faceURL,
                                birth = selfInfo.birth,
                                gender = selfInfo.gender,
                                email = selfInfo.email,
                                token = token.token,
                                expiredTime = token.expiredTime
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.w("Login", "Error :  ",  e)
            }
        }
    }
}