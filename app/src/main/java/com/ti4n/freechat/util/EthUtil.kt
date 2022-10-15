package com.ti4n.freechat.util

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.ERC20Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.withContext
import org.kethereum.DEFAULT_ETHEREUM_BIP44_PATH
import org.kethereum.bip32.toKey
import org.kethereum.bip39.generateMnemonic
import org.kethereum.bip39.model.MnemonicWords
import org.kethereum.bip39.toSeed
import org.kethereum.bip39.wordlists.WORDLIST_ENGLISH
import org.kethereum.crypto.toAddress
import org.kethereum.model.Address
import org.kethereum.model.PrivateKey
import org.kethereum.model.PublicKey
import org.kethereum.rpc.HttpEthereumRPC
import org.komputing.kethereum.erc20.ERC20RPCConnector
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.RawTransaction
import org.web3j.crypto.TransactionEncoder
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.request.Transaction
import org.web3j.protocol.core.methods.response.EthEstimateGas
import org.web3j.protocol.core.methods.response.EthGasPrice
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigInteger
import kotlin.math.pow


object EthUtil {

    val web3 =
        Web3j.build(HttpService("https://mainnet.infura.io/v3/a36c7f54cb3244a3b352f922daad690c"))

    val rpc = HttpEthereumRPC("https://mainnet.infura.io/v3/a36c7f54cb3244a3b352f922daad690c")

    fun mnemonicWordsExist(words: String) =
        WalletUtils.isValidAddress(MnemonicWords(words).address().hex)

    fun getMnemonicCode(words: String = "") =
        if (words == "") MnemonicWords(generateMnemonic(wordList = WORDLIST_ENGLISH)) else MnemonicWords(
            words
        )

    suspend fun createWalletFile(context: Context, password: String, mnemonicWords: String) {
        val file = WalletUtils.generateWalletFile(
            password,
            MnemonicWords(mnemonicWords).toKeyPair().toWeb3ECKeyPair(),
            context.cacheDir,
            false
        )
        context.dataStore.edit {
            it[stringPreferencesKey("account")] = mnemonicWords
            it[stringPreferencesKey("file")] = file
            it[stringPreferencesKey("address")] = MnemonicWords(mnemonicWords).address().hex
        }
    }

    suspend fun loadCredentials(context: Context, password: String) = withContext(Dispatchers.IO) {
        WalletUtils.loadCredentials(
            password, File(
                context.cacheDir,
                context.dataStore.data.map { it[stringPreferencesKey("file")] }.filterNotNull()
                    .first()
            )
        )
    }

    suspend fun gasPrice() = withContext(Dispatchers.IO) {
        rpc.gasPrice()
    }

    suspend fun transactionCount(address: String) = withContext(Dispatchers.IO) {
        rpc.getTransactionCount(Address(address))
    }

    suspend fun transferERC20(
        context: Context,
        to: String,
        amount: String,
        tokenAddress: String,
        decimal: Int,
        password: String = ""
    ) = ERC20.load(
        tokenAddress, web3, loadCredentials(context, password), DefaultGasProvider()
    ).transfer(to, BigInteger.valueOf((amount.toDouble() * (10.0.pow(decimal))).toLong()))
        .flowable().asFlow().flowOn(Dispatchers.IO)

    suspend fun transfer(
        context: Context,
        to: String = "0xf3a988124c1985ac2c61624ff686d06e16818ea4",
        amount: String = "0.001",
        password: String = ""
    ): Flow<EthGetTransactionReceipt> {
        val credentials = loadCredentials(context, password)
        val value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()
        Log.e("transfer", "transfer: ${credentials.address}")
        val ethGetTransactionCount = transactionCount(credentials.address)
        val gasPrice = gasPrice()
        val rawTransaction = RawTransaction.createEtherTransaction(
            ethGetTransactionCount, gasPrice, BigInteger.valueOf(21000), to, value
        )
        val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
        val hexValue = Numeric.toHexString(signedMessage)
        return web3.ethSendRawTransaction(hexValue).flowable().asFlow().flatMapLatest {
            Log.e("transfer", "transfer: ${it.transactionHash}")
            Log.e("transfer", "transfer: ${it.error}")
            web3.ethGetTransactionReceipt(it.transactionHash).flowable().asFlow()
        }.flowOn(Dispatchers.IO)
    }

    suspend fun balanceOf(
        token: ERC20Token, address: String
    ) = withContext(Dispatchers.IO) {
        ERC20RPCConnector(Address(token.contractAddress), rpc).balanceOf(Address(address))
            ?.toWei(token.Decimals)
    }

    suspend fun balanceOf(address: String) = withContext(Dispatchers.IO) {
        rpc.getBalance(Address(address))?.toWei(18)
    }

    suspend fun gasPrice(from: String, to: String): Flow<Pair<String, String>?> =
        web3.ethGasPrice().flowable().asFlow().combine(
            web3.ethEstimateGas(
                Transaction.createEthCallTransaction(
                    from, to, "0xd0e30db0"
                )
            ).flowable().asFlow()
        ) { a: EthGasPrice?, b: EthEstimateGas? ->
            return@combine if (a != null && b != null) {
                "${((b.amountUsed.toDouble() + 2000) * a.gasPrice.toDouble()).toWei(18)}" to "${
                    (b.amountUsed.toDouble() * a.gasPrice.toDouble()).toWei(18)
                }"
            } else null
        }.flowOn(Dispatchers.IO)

}

fun MnemonicWords.toKeyPair(walletIndex: Int = 0) =
    toSeed().toKey(DEFAULT_ETHEREUM_BIP44_PATH).keyPair

fun MnemonicWords.privateKey(walletIndex: Int = 0) = toKeyPair(walletIndex).privateKey
fun MnemonicWords.address(walletIndex: Int = 0) = toKeyPair().toAddress()

fun BigInteger.toWei(decimal: Int) =
    (toDouble() / (10.0.pow(decimal))).toBigDecimal().toPlainString()

fun Double.toWei(decimal: Int) = this / 10.0.pow(decimal)

fun org.kethereum.model.Credentials.toWeb3Credentials() =
    Credentials.create(ECKeyPair((ecKeyPair?.privateKey?.key), (ecKeyPair?.publicKey?.key)))

fun org.kethereum.model.ECKeyPair.toWeb3ECKeyPair() = ECKeyPair(privateKey.key, publicKey.key)

fun ECKeyPair.toKECKeyPair() =
    org.kethereum.model.ECKeyPair(PrivateKey(privateKey), PublicKey(publicKey))
