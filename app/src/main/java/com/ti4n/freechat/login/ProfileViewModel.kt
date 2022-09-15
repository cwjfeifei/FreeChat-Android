package com.ti4n.freechat.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    val avatar = MutableStateFlow("")
    val name = MutableStateFlow("")
    val birthday = MutableStateFlow(0L)
    val gender = MutableStateFlow("")
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

    fun setGender(gender: String) {
        this.gender.value = gender
    }

    fun setCountry(country: String) {
        this.country.value = country
    }

    fun setLocation(location: String) {
        this.location.value = location
    }
}