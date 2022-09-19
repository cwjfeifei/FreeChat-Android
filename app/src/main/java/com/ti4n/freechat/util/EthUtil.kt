package com.ti4n.freechat.util

import org.kethereum.DEFAULT_ETHEREUM_BIP44_PATH
import org.kethereum.bip32.toExtendedKey
import org.kethereum.bip32.toKey
import org.kethereum.bip39.generateMnemonic
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH
import org.kethereum.crypto.toAddress

object EthUtil {

    fun getMnemonicCode(words: String = "") =
        if (words == "") MnemonicWords(generateMnemonic(wordList = WORDLIST_ENGLISH)) else MnemonicWords(
            words
        )

    fun transfer(to: String, amount: Long) {

    }
}

fun MnemonicWords.toKeyPair(walletIndex: Int = 0) = toSeed().toKey(DEFAULT_ETHEREUM_BIP44_PATH).keyPair

fun MnemonicWords.privateKey(walletIndex: Int = 0) = toKeyPair(walletIndex).privateKey
fun MnemonicWords.address(walletIndex: Int = 0) = toKeyPair().toAddress()
