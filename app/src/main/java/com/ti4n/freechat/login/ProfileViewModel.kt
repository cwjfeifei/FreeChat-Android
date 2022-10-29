package com.ti4n.freechat.login

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.db.UserBaseInfo
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.model.request.Register
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.util.Minio
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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

    fun register(address: String, avatar: String, password: String, success: () -> Unit) {
        viewModelScope.launch {
            try {
                apiService.register(
                    Register(
                        address,
                        birthday.value / 1000,
                        avatar,
                        gender.value,
                        name.value,
                        password
                    )
                ).data?.let { userToken ->
                    context.dataStore.edit {
                        it[stringPreferencesKey("token")] = userToken.token
                        it[stringPreferencesKey("userId")] = userToken.userID
                    }
                    dataBase.userBaseInfoDao().insert(
                        UserBaseInfo(
                            userToken.userID,
                            name.value,
                            userToken.token,
                            avatar,
                            birthday.value / 1000
                        )
                    )
                    success()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}