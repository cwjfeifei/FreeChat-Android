package com.ti4n.freechat.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.util.EthUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val imService: FreeChatIMService,
    val db: AppDataBase
) : ViewModel() {

    val word1 = mutableStateOf("")
    val word2 = mutableStateOf("")
    val word3 = mutableStateOf("")
    val word4 = mutableStateOf("")
    val word5 = mutableStateOf("")
    val word6 = mutableStateOf("")
    val word7 = mutableStateOf("")
    val word8 = mutableStateOf("")
    val word9 = mutableStateOf("")
    val word10 = mutableStateOf("")
    val word11 = mutableStateOf("")
    val word12 = mutableStateOf("")

    fun wordsIsCorrect() =
        EthUtil.mnemonicWordsExist("${word1.value} ${word2.value} ${word3.value} ${word4.value} ${word5.value} ${word6.value} ${word7.value} ${word8.value} ${word9.value} ${word10.value} ${word11.value} ${word12.value}")
}