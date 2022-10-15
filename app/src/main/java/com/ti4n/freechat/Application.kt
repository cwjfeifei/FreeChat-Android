package com.ti4n.freechat

import com.ti4n.freechat.util.EthUtil
import dagger.hilt.android.HiltAndroidApp
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security


@HiltAndroidApp
class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        setupBouncyCastle()
    }

    private fun setupBouncyCastle() {
        val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            ?: // Web3j will set up a provider  when it's used for the first time.
            return
        if (provider::class.java == BouncyCastleProvider::class.java) {
            return
        }
        //There is a possibility  the bouncy castle registered by android may not have all ciphers
        //so we  substitute with the one bundled in the app.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }
}