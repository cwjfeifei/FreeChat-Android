package com.ti4n.freechat.login

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.ti4n.freechat.Route
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.db.UserBaseInfo
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.model.request.GetSelfInfo
import com.ti4n.freechat.model.request.GetToken
import com.ti4n.freechat.model.request.Register
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.network.bage.Parameter
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.address
import com.ti4n.freechat.util.md5
import dagger.hilt.android.lifecycle.HiltViewModel
import io.openim.android.sdk.OpenIMClient
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

//    val navigationRoute = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 1)
    val words = MutableStateFlow(EthUtil.getMnemonicCode().words)
    val shuffledWord = MutableStateFlow(emptyList<String>())
    val clickedWords = MutableStateFlow(emptyList<String>())

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

    private fun getParameter(email: String, password: String): Parameter {
        val parameter: Parameter = Parameter()
            .add("password", md5(password)!!)
            .add("platform", 2)
            .add("operationID", System.currentTimeMillis().toString() + "")
            .add("email", email)
        // No verificationCode and phone
//            .add("verificationCode", verificationCode)
//        if (isPhone.getValue()) {
//            parameter.add("phoneNumber", account.getValue())
//            parameter.add("areaCode", "+86")
//        } else
//            parameter.add("email", email)
        return parameter
    }

    fun registerFreeChat(context: Context, navController: NavController, words:String, email: String, password: String ) {
        var userID = MnemonicWords(words).address().hex
        Log.d(TAG, "registerFreeChat: " + userID+", " + email)
        val parameter = getParameter(email, password)
        viewModelScope.launch {
            try {
                val response = imService.register(
                    Register(
                        userID,
                        0,
                        "",
                        1,
                        "",
                        password,
                        email= email,
                    )
                )
                if (response.errCode == 0 && response.data != null) {
                    context.dataStore.edit {
                        it[stringPreferencesKey("token")] = response.data.token
                        it[stringPreferencesKey("userId")] = response.data.userID
                    }
                    db.userBaseInfoDao().insert(
                        UserBaseInfo(
                            response.data.userID,
                            "",
                            response.data.token,
                            "",
                            0
                        )
                    )
                    // register success
                    EthUtil.createWalletFile(context, email, password, words)
//                    navigationRoute.emit(Route.Home.route)
                    navController.navigate(Route.CompleteProfile.route)
                } else {
                    navController.navigate(Route.CompleteProfile.route)
//                    navigationRoute.emit(Route.CompleteProfile.route)
                }
            } catch (e: Exception) {
//                navigationRoute.emit(Route.CompleteProfile.route)
                // debug -
                navController.navigate(Route.CompleteProfile.route)
                e.printStackTrace()
            }
        }
//        OpenIMClient.getInstance().userInfoManager.setSelfInfo()
    }
}