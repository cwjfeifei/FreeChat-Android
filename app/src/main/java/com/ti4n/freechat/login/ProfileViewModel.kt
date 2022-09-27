package com.ti4n.freechat.login

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.model.request.Register
import com.ti4n.freechat.network.FreeChatApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val apiService: FreeChatApiService,
    @ApplicationContext val context: Context
) : ViewModel() {

    val avatar = MutableStateFlow("")
    val name = MutableStateFlow("")
    val birthday = MutableStateFlow(0L)
    val gender = MutableStateFlow(-1)
    val country = MutableStateFlow("")
    val location = MutableStateFlow("")

    fun setAvatar(avatar: String) {
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

    fun register(publicKey: String, address: String, password: String) {
        viewModelScope.launch {
            val userToken = apiService.register(
                Register(
                    address,
                    birthday.value,
                    "",
                    gender.value,
                    name.value,
                    password,
                    publicKey
                )
            ).data
            context.dataStore.edit {
                it[stringPreferencesKey("token")] = userToken.token
                it[stringPreferencesKey("userId")] = userToken.userID
            }
        }
    }
}