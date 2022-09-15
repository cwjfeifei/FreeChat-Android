package com.ti4n.freechat.login

import androidx.lifecycle.ViewModel
import com.ti4n.freechat.util.TronUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {

    val words = MutableStateFlow(TronUtil.getMnemonicCode().toList())
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
}