package com.ti4n.freechat.util

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.reactive.asFlow
import org.kethereum.DEFAULT_ETHEREUM_BIP44_PATH
import org.kethereum.bip32.toKey
import org.kethereum.bip39.generateMnemonic
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH
import org.kethereum.crypto.toAddress
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthEstimateGas
import org.web3j.protocol.core.methods.response.EthGasPrice
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.math.BigInteger
import kotlin.math.pow


object EthUtil {

    lateinit var web3: Web3j

    fun initWeb3j() {
        web3 =
            Web3j.build(HttpService("https://goerli.infura.io/v3/a36c7f54cb3244a3b352f922daad690c"))
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

    fun transfer(
        to: String = "0xf3a988124c1985ac2c61624ff686d06e16818ea4",
        amount: String = "0.001",
        accountWords: MnemonicWords = MnemonicWords("crumble forest crop trick rescue light patient talk flock balcony labor ball")
    ): Flow<EthGetTransactionReceipt> {
        val credentials = Credentials.create(accountWords.privateKey().key.toString(16))
        Log.e("address", "transfer: ${credentials.address}")
        val value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()
        val ethGetTransactionCount = web3
            .ethGetTransactionCount(credentials.address, DefaultBlockParameterName.LATEST)
            .flowable().asFlow()
        val gasPrice = web3.ethGasPrice().flowable().asFlow()
        return ethGetTransactionCount.combine(gasPrice) { count, price ->
            count.transactionCount to price.gasPrice
        }.flatMapLatest {
            val rawTransaction = RawTransaction.createEtherTransaction(
                it.first, it.second, BigInteger.valueOf(21000),
                to, value
            )
            val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
            val hexValue = Numeric.toHexString(signedMessage)
            web3.ethSendRawTransaction(hexValue).flowable().asFlow()
        }.flatMapLatest {
            web3.ethGetTransactionReceipt(it.transactionHash).flowable().asFlow()
        }
    }

    fun balanceOf(tokenAddress: String, accountWords: MnemonicWords) =
        ERC20.load(
            tokenAddress,
            web3,
            Credentials.create(accountWords.privateKey().key.toString()),
            DefaultGasProvider()
        ).balanceOf(accountWords.address().hex).flowable().asFlow()

    fun balanceOf(address: String) = web3.ethGetBalance(
        address,
        DefaultBlockParameterName.LATEST
    ).flowable().asFlow().onEach {
        Log.e("balance", "balanceOf: ${it.balance}")
    }

    fun gasPrice(from: String, to: String): Flow<Pair<String, String>?> =
        web3.ethGasPrice().flowable().asFlow().combine(
            web3.ethEstimateGas(
                Transaction.createEthCallTransaction(
                    from,
                    to,
                    "0xd0e30db0"
                )
            ).flowable().asFlow()
        ) { a: EthGasPrice?, b: EthEstimateGas? ->
            return@combine if (a != null && b != null) {
                "${((b.amountUsed.toDouble() + 2000) * a.gasPrice.toDouble()).toWei(18)}" to "${
                    (b.amountUsed.toDouble() * a.gasPrice.toDouble()).toWei(18)
                }"
            } else null
        }
}

fun MnemonicWords.toKeyPair(walletIndex: Int = 0) =
    toSeed().toKey(DEFAULT_ETHEREUM_BIP44_PATH).keyPair

fun MnemonicWords.privateKey(walletIndex: Int = 0) = toKeyPair(walletIndex).privateKey
fun MnemonicWords.address(walletIndex: Int = 0) = toKeyPair().toAddress()

fun BigInteger.toWei(decimal: Int) = toDouble() / 10.0.pow(decimal)
fun Double.toWei(decimal: Int) = this / 10.0.pow(decimal)
