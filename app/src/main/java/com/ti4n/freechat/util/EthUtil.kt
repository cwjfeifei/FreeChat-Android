package com.ti4n.freechat.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.collect
import kotlinx.coroutines.withContext
import org.kethereum.DEFAULT_ETHEREUM_BIP44_PATH
import org.kethereum.bip32.toExtendedKey
import org.kethereum.bip32.toKey
import org.kethereum.bip39.generateMnemonic
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH
import org.kethereum.crypto.toAddress
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.pow

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

    fun transfer(to: String, amount: String, tokenAddress: String, accountWords: MnemonicWords) =
        ERC20.load(
            tokenAddress,
            web3,
            Credentials.create(accountWords.privateKey().key.toString()),
            DefaultGasProvider()
        ).transfer(to, BigInteger(amount)).flowable().asFlow()

    fun balanceOf(tokenAddress: String, accountWords: MnemonicWords) =
        ERC20.load(
            tokenAddress,
            web3,
            Credentials.create(accountWords.privateKey().key.toString()),
            DefaultGasProvider()
        ).balanceOf(accountWords.address().hex).flowable().asFlow()
}

fun MnemonicWords.toKeyPair(walletIndex: Int = 0) =
    toSeed().toKey(DEFAULT_ETHEREUM_BIP44_PATH).keyPair

fun MnemonicWords.privateKey(walletIndex: Int = 0) = toKeyPair(walletIndex).privateKey
fun MnemonicWords.address(walletIndex: Int = 0) = toKeyPair().toAddress()

fun BigInteger.toWei(decimal: Int) = toDouble() / 10.0.pow(decimal)
