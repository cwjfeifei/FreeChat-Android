package com.ti4n.freechat.util

import com.ti4n.freechat.network.freeChatUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

suspend fun pingFreeChat() = withContext(Dispatchers.IO) {
    suspendCancellableCoroutine {
        it.resume(Runtime.getRuntime().exec("ping -c 1 -w 100 https://freechat.world/").waitFor() == 0)
    }
}