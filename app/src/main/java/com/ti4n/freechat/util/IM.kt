package com.ti4n.freechat.util

import android.content.Context
import android.util.Log
import io.openim.android.sdk.OpenIMClient
import io.openim.android.sdk.listener.OnAdvanceMsgListener
import io.openim.android.sdk.listener.OnBase
import io.openim.android.sdk.listener.OnConnListener
import io.openim.android.sdk.models.Message
import io.openim.android.sdk.models.ReadReceiptInfo
import io.openim.android.sdk.models.UserInfo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import open_im_sdk.Open_im_sdk
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object IM {

    private val imClient = OpenIMClient.getInstance()
    val currentUserInfo = MutableStateFlow(UserInfo())
    val newMessages = MutableStateFlow(listOf<Message>())

    fun init(context: Context) {
        imClient.initSDK(
            "http://47.57.185.242:10002",
            "ws://47.57.185.242:10001",
            context.filesDir.absolutePath,
            6,
            "cos",
            object : OnConnListener {
                override fun onConnectFailed(code: Long, error: String?) {
                    Log.e("initIM", "onConnectFailed: $code $error")
                }

                override fun onConnectSuccess() {
                    Log.e("initIM", "连接成功")
                }

                override fun onConnecting() {
                    Log.e("initIM", "连接中")
                }

                override fun onKickedOffline() {
                    TODO("Not yet implemented")
                }

                override fun onUserTokenExpired() {
                    TODO("Not yet implemented")
                }

            })
        setListener()
//        login()
    }

    fun login() {
        imClient.login(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {

            }

            override fun onSuccess(data: String?) {

            }
        }, "", "")
    }

    fun setListener() {
        imClient.userInfoManager.setOnUserListener {
            currentUserInfo.value = it
        }
        imClient.messageManager.setAdvancedMsgListener(object : OnAdvanceMsgListener {
            override fun onRecvNewMessage(msg: Message?) {
                if (msg != null) {
                    newMessages.value = newMessages.value + msg
                }
            }

            override fun onRecvC2CReadReceipt(list: MutableList<ReadReceiptInfo>?) {
                TODO("Not yet implemented")
            }

            override fun onRecvGroupMessageReadReceipt(list: MutableList<ReadReceiptInfo>?) {
                TODO("Not yet implemented")
            }

            override fun onRecvMessageRevoked(msgId: String?) {
                if (msgId != null) {
                    val messages = newMessages.value.toMutableList()
                    messages.removeAll { it.serverMsgID == msgId }
                    newMessages.value = messages
                }
            }
        })
    }

    suspend fun getHistoryMessages(startMsg: Message?) = suspendCoroutine {
        imClient.messageManager.getHistoryMessageListReverse(
            object : OnBase<List<Message>> {
                override fun onError(code: Int, error: String?) {
                    it.resume(listOf<Message>())
                }

                override fun onSuccess(data: List<Message>?) {
                    it.resume(data ?: listOf())
                }

            },
            "", "", startMsg, 20
        )
    }
}

