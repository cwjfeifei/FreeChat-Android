package com.ti4n.freechat.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ti4n.freechat.R
import com.ti4n.freechat.db.UserBaseInfoDao
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
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
import org.web3j.protocol.core.methods.response.EthSendTransaction
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.DefaultGasProvider
import org.web3j.utils.Convert
import org.web3j.utils.Numeric
import java.io.File
import java.math.BigInteger
import kotlin.math.pow


object EthUtil {

    val web3 =
        Web3j.build(HttpService("https://goerli.infura.io/v3/a36c7f54cb3244a3b352f922daad690c"))

    val rpc = HttpEthereumRPC("https://goerli.infura.io/v3/a36c7f54cb3244a3b352f922daad690c")

    fun mnemonicWordsExist(words: String) =
        WalletUtils.isValidAddress(MnemonicWords(words).address().hex)

    fun addressExist(address: String) =
        WalletUtils.isValidAddress(address)

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
            it[stringPreferencesKey("file")] = file
            it[stringPreferencesKey("address")] = MnemonicWords(mnemonicWords).address().hex
        }
    }

    suspend fun deleteWallet(context: Context, userBaseInfoDao: UserBaseInfoDao) {
        val file =
            context.dataStore.data.map { it[stringPreferencesKey("file")] }.filterNotNull().first()
        File(context.cacheDir, file).delete()
        context.dataStore.edit {
            it[stringPreferencesKey("address")] = ""
            it[stringPreferencesKey("file")] = ""
        }
        IM.logout()
        userBaseInfoDao.delete()
    }

    suspend fun loadCredentials(context: Context, password: String) = withContext(Dispatchers.IO) {
        try {
            WalletUtils.loadCredentials(
                password, File(
                    context.cacheDir,
                    context.dataStore.data.map { it[stringPreferencesKey("file")] }.filterNotNull()
                        .first()
                )
            )
        } catch (e: Exception) {
            toast.emit(R.string.wrong_password)
            null
        }
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
    ).transfer(
        to,
        BigInteger((amount.toDouble() * (10.0.pow(decimal))).toBigDecimal().toPlainString())
    )
        .flowable().asFlow().catch { it.printStackTrace() }.flowOn(Dispatchers.IO)

    suspend fun transfer(
        context: Context,
        to: String,
        amount: String,
        password: String = ""
    ): Flow<EthSendTransaction>? {
        val credentials = loadCredentials(context, password)
        if (credentials != null) {
            val value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger()
            val ethGetTransactionCount = transactionCount(credentials.address)
            val gasPrice = gasPrice()
            val rawTransaction = RawTransaction.createEtherTransaction(
                ethGetTransactionCount, gasPrice, BigInteger.valueOf(21000), to, value
            )
            val signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials)
            val hexValue = Numeric.toHexString(signedMessage)
            return web3.ethSendRawTransaction(hexValue).flowable().asFlow().flowOn(Dispatchers.IO)
        } else {
            return null
        }
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

    fun gasPrice(from: String, to: String): Flow<Pair<String, String>?> =
        web3.ethGasPrice().flowable().asFlow().combine(
            web3.ethEstimateGas(
                Transaction.createEthCallTransaction(
                    from, to, "0xd0e30db0"
                )
            ).flowable().asFlow()
        ) { a: EthGasPrice?, b: EthEstimateGas? ->
            return@combine if (a != null && b != null) {
                "${((b.amountUsed.toDouble() + 2000) * a.gasPrice.toDouble()).toWei(18)}" to (b.amountUsed.toDouble() * a.gasPrice.toDouble()).toWei(
                    18
                )
            } else null
        }.flowOn(Dispatchers.IO)

}

fun MnemonicWords.toKeyPair() = toSeed().toKey(DEFAULT_ETHEREUM_BIP44_PATH).keyPair

fun MnemonicWords.privateKey() = toKeyPair().privateKey
fun MnemonicWords.address() = toKeyPair().toAddress()

fun BigInteger.toWei(decimal: Int) =
    (toDouble() / (10.0.pow(decimal))).toBigDecimal().toPlainString()

fun Double.toWei(decimal: Int) = (this / 10.0.pow(decimal)).toBigDecimal().toPlainString()
fun Float.toWei(decimal: Int) = (this / 10.0.pow(decimal)).toBigDecimal().toPlainString()

fun org.kethereum.model.Credentials.toWeb3Credentials() =
    Credentials.create(ECKeyPair((ecKeyPair?.privateKey?.key), (ecKeyPair?.publicKey?.key)))

fun org.kethereum.model.ECKeyPair.toWeb3ECKeyPair() = ECKeyPair(privateKey.key, publicKey.key)

fun ECKeyPair.toKECKeyPair() =
    org.kethereum.model.ECKeyPair(PrivateKey(privateKey), PublicKey(publicKey))
