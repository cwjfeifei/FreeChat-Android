package com.ti4n.freechat

import com.ti4n.freechat.util.EthUtil
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()
        EthUtil.initWeb3j()
    }
}