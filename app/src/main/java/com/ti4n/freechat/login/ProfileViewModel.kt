package com.ti4n.freechat.login

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.network.FreeChatIMService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.openim.android.sdk.OpenIMClient
import io.openim.android.sdk.listener.OnBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val apiService: FreeChatIMService,
    val dataBase: AppDataBase,
    @ApplicationContext val context: Context
) : ViewModel() {

    val avatar = MutableStateFlow<Uri?>(null)
    val name = MutableStateFlow("")
    val birthday = MutableStateFlow(0L)
    val gender = MutableStateFlow(-1)
    val country = MutableStateFlow("")
    val location = MutableStateFlow("")

    fun setAvatar(avatar: Uri) {
        this.avatar.value = avatar
    }

    fun setName(name: String) {
        this.name.value = name
    }

    fun setBirthday(birthday: Long) {
        this.birthday.value = birthday
    }

    fun setGender(gender: Int) {
        this.gender.value = gender
    }

    fun setCountry(country: String) {
        this.country.value = country
    }

    fun setLocation(location: String) {
        this.location.value = location
    }

    /**
     * @param nickname    名字
     * @param faceURL     头像
     * @param gender      1 male, 2 female
     * @param birth       出生日期
     * @param email       邮箱
     * @param base        callback String
     */
    fun setSelfInfo(
        nickname: String?, faceURL: String?, gender: Int,
        birth: Long, email: String?, ex: String?
    ) {
        val callBack: OnBase<String> = object : OnBase<String> {
            override fun onError(code: Int, error: String) {
            }

            override fun onSuccess(data: String?) {
            }
        }
        viewModelScope.launch {
            // 	appMangerLevel { AppOrdinaryUsers = 1, AppAdmin = 2}
            OpenIMClient.getInstance().userInfoManager.setSelfInfo(
                callBack,
                nickname,
                faceURL,
                gender,
                1,
                "",
                birth,
                email,
                ex
            )
        }
    }

//    fun register(address: String, avatar: String, password: String, success: () -> Unit) {
//        viewModelScope.launch {
//            try {
//                apiService.register(
//                    Register(
//                        address,
//                        birthday.value / 1000,
//                        avatar,
//                        gender.value,
//                        name.value,
//                        password
//                    )
//                ).data?.let { userToken ->
//                    context.dataStore.edit {
//                        it[stringPreferencesKey("token")] = userToken.token
//                        it[stringPreferencesKey("userId")] = userToken.userID
//                    }
//                    dataBase.userBaseInfoDao().insert(
//                        UserBaseInfo(
//                            userToken.userID,
//                            name.value,
//                            userToken.token,
//                            avatar,
//                            birthday.value / 1000
//                        )
//                    )
//                    success()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
}