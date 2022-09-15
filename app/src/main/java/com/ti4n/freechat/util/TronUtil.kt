package com.ti4n.freechat.util

import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import com.ti4n.freechat.bip44forandroidlibrary.utils.Bip44Utils
import com.ti4n.freechat.bip44forandroidlibrary.utils.Utils
import org.tron.trident.core.ApiWrapper
import org.tron.trident.core.contract.Trc20Contract
import org.tron.trident.core.key.KeyPair
import org.tron.trident.core.transaction.TransactionBuilder
import org.tron.trident.proto.Chain.Transaction
import org.tron.trident.proto.Contract

object TronUtil {

    fun getMnemonicCode(words: String = "") =
        if (words == "") Mnemonics.MnemonicCode(Mnemonics.WordCount.COUNT_12) else Mnemonics.MnemonicCode(
            words
        )

    fun transfer(to: String, amount: Long) {

    }

    fun trc20Contract(keyPair: KeyPair): Trc20Contract {
        val wrapper = ApiWrapper.ofShasta(keyPair.toPrivateKey())
        return Trc20Contract(
            wrapper
                .getContract("TMtuE3a2vC74omqxjmBGD8soYmbgFENZ1Y"),
            keyPair.toBase58CheckAddress(),
            wrapper
        )
    }
}

fun Mnemonics.MnemonicCode.toKeyPair(walletIndex: Int = 0) = KeyPair(
    Utils.HEX.encode(
        Utils.bigIntegerToBytes(
            Bip44Utils.getPathPrivateKey(
                toList(),
                toSeed(),
                "m/44'/195'/0'/0/$walletIndex"
            ), 32
        )
    )
)

fun Mnemonics.MnemonicCode.privateKey(walletIndex: Int = 0) = toKeyPair(walletIndex).toPrivateKey()
fun Mnemonics.MnemonicCode.address(walletIndex: Int = 0) =
    toKeyPair(walletIndex).toBase58CheckAddress()

fun KeyPair.trc20Contract(): Trc20Contract {
    val wrapper = ApiWrapper.ofNile(toPrivateKey())
    return Trc20Contract(
        wrapper
            .getContract("TDmdMAi1nKPfA3JeBf4jJ9jrTVBR9uFi5f"),
        toBase58CheckAddress(),
        wrapper
    )
}