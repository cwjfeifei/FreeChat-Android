package com.ti4n.freechat.util

import org.kethereum.DEFAULT_ETHEREUM_BIP44_PATH
import org.kethereum.bip32.toExtendedKey
import org.kethereum.bip32.toKey
import org.kethereum.bip39.generateMnemonic
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH
import org.kethereum.crypto.toAddress
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService

object EthUtil {

    lateinit var web3: Web3j

    fun initWeb3j() {
        web3 =
            Web3j.build(HttpService("https://mainnet.infura.io/v3/a36c7f54cb3244a3b352f922daad690c"))
    }

    fun mnemonicWordsExist(words: String) =
        WalletUtils.isValidAddress(MnemonicWords(words).address().hex)

    fun getMnemonicCode(words: String = "") =
        if (words == "") MnemonicWords(generateMnemonic(wordList = WORDLIST_ENGLISH)) else MnemonicWords(
            words
        )

    fun transfer(to: String, amount: Long) {

    }
}

fun MnemonicWords.toKeyPair(walletIndex: Int = 0) =
    toSeed().toKey(DEFAULT_ETHEREUM_BIP44_PATH).keyPair

fun MnemonicWords.privateKey(walletIndex: Int = 0) = toKeyPair(walletIndex).privateKey
fun MnemonicWords.address(walletIndex: Int = 0) = toKeyPair().toAddress()
