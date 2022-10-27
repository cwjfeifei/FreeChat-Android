package com.ti4n.freechat.util

import android.content.Context
import android.util.Log
import com.ti4n.freechat.db.UserBaseInfo
import com.ti4n.freechat.db.UserBaseInfoDao
import com.ti4n.freechat.model.response.UserToken
import com.ti4n.freechat.model.response.freechat.SelfInfo
import io.openim.android.sdk.OpenIMClient
import io.openim.android.sdk.listener.OnAdvanceMsgListener
import io.openim.android.sdk.listener.OnBase
import io.openim.android.sdk.listener.OnConnListener
import io.openim.android.sdk.listener.OnConversationListener
import io.openim.android.sdk.listener.OnFileUploadProgressListener
import io.openim.android.sdk.listener.OnFriendshipListener
import io.openim.android.sdk.listener.OnMsgSendCallback
import io.openim.android.sdk.models.BlacklistInfo
import io.openim.android.sdk.models.ConversationInfo
import io.openim.android.sdk.models.FriendApplicationInfo
import io.openim.android.sdk.models.FriendInfo
import io.openim.android.sdk.models.Message
import io.openim.android.sdk.models.OfflinePushInfo
import io.openim.android.sdk.models.ReadReceiptInfo
import io.openim.android.sdk.models.RevokedInfo
import io.openim.android.sdk.models.UserInfo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import open_im_sdk.Open_im_sdk
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object IM {

    private val imClient = OpenIMClient.getInstance()
    val currentUserInfo = MutableStateFlow(UserInfo())
    val newMessages = MutableStateFlow(listOf<Message>())
    val conversations = mutableListOf<ConversationInfo>()
    val totalUnreadCount = MutableStateFlow(0)

    fun init(context: Context) {
        imClient.initSDK(7,
            "http://47.57.185.242:10002",
            "ws://47.57.185.242:10001",
            context.cacheDir.absolutePath,
            6,
            "oss",
            null,
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
                }

                override fun onUserTokenExpired() {
                }

            })
        setListener()
    }

    suspend fun login(userId: String, token: String) = suspendCoroutine {
        imClient.login(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: String?) {
                getAllConversations()
                it.resume(data)
            }
        }, userId, token)
    }

    suspend fun logout() = suspendCoroutine {
        imClient.logout(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {
                it.resume(Unit)
            }

            override fun onSuccess(data: String?) {
                it.resume(Unit)
            }

        })
    }

    fun setListener() {
        imClient.userInfoManager.setOnUserListener {
            currentUserInfo.value = it
        }
        imClient.conversationManager.setOnConversationListener(object : OnConversationListener {
            override fun onConversationChanged(list: MutableList<ConversationInfo>?) {
                list?.forEach { update ->
                    conversations.removeAll { it.conversationID == update.conversationID }
                    conversations.add(update)
                }
            }

            override fun onNewConversation(list: MutableList<ConversationInfo>?) {
                list?.let {
                    conversations.addAll(it)
                }
            }

            override fun onSyncServerFailed() {
            }

            override fun onSyncServerFinish() {
            }

            override fun onSyncServerStart() {

            }

            override fun onTotalUnreadMessageCountChanged(i: Int) {
                totalUnreadCount.value = i
            }
        })
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

            }

            override fun onRecvMessageRevokedV2(info: RevokedInfo?) {
                if (info != null) {
                    val messages = newMessages.value.toMutableList()
                    messages.removeAll { it.clientMsgID == info.clientMsgID }
                    newMessages.value = messages
                }
            }
        })
        imClient.friendshipManager.setOnFriendshipListener(object : OnFriendshipListener {
            override fun onBlacklistAdded(u: BlacklistInfo?) {
                TODO("Not yet implemented")
            }

            override fun onBlacklistDeleted(u: BlacklistInfo?) {
                TODO("Not yet implemented")
            }

            override fun onFriendApplicationAccepted(u: FriendApplicationInfo?) {
                TODO("Not yet implemented")
            }

            override fun onFriendApplicationAdded(u: FriendApplicationInfo?) {
                TODO("Not yet implemented")
            }

            override fun onFriendApplicationDeleted(u: FriendApplicationInfo?) {
                TODO("Not yet implemented")
            }

            override fun onFriendApplicationRejected(u: FriendApplicationInfo?) {
                TODO("Not yet implemented")
            }

            override fun onFriendInfoChanged(u: FriendInfo?) {
                TODO("Not yet implemented")
            }

            override fun onFriendAdded(u: FriendInfo?) {
                TODO("Not yet implemented")
            }

            override fun onFriendDeleted(u: FriendInfo?) {
                TODO("Not yet implemented")
            }
        })
    }

    suspend fun uploadFile(imagePath: String) = suspendCoroutine {
        imClient.uploadFile(object : OnFileUploadProgressListener {
            override fun onError(code: Int, error: String?) {
                Log.e("error", "onError: $error")
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(s: String) {
                it.resume(s)
            }

            override fun onProgress(progress: Long) {

            }

        }, imagePath)
    }

    suspend fun setUserInfo(nickname: String, faceURL: String, gender: Int, birth: Long) =
        suspendCoroutine {
            imClient.userInfoManager.setSelfInfo(
                object : OnBase<String> {
                    override fun onError(code: Int, error: String?) {
                        it.resumeWithException(IMError(code, error))
                    }

                    override fun onSuccess(data: String?) {
                        it.resume(Unit)
                    }
                }, nickname, faceURL, gender, 0, "", birth, "", ""
            )
        }

    suspend fun getHistoryMessages(startMsg: Message?, conversationId: String) = suspendCoroutine {
        imClient.messageManager.getHistoryMessageListReverse(
            object : OnBase<List<Message>> {
                override fun onError(code: Int, error: String?) {
                    it.resume(listOf<Message>())
                }

                override fun onSuccess(data: List<Message>?) {
                    it.resume(data ?: listOf())
                }

            }, "", "", conversationId, startMsg, 20
        )
    }

    fun getAllConversations() {
        imClient.conversationManager.getAllConversationList(object :
            OnBase<List<ConversationInfo>> {
            override fun onError(code: Int, error: String?) {

            }

            override fun onSuccess(data: List<ConversationInfo>?) {
                data?.let {
                    conversations.addAll(it)
                }
            }

        })
    }

    fun addContact(address: String) {

    }

    fun startConversion(address: String) {

    }

    suspend fun sendTextMessage(address: String, content: String) = suspendCoroutine {
        imClient.messageManager.sendMessage(
            object : OnMsgSendCallback {
                override fun onError(code: Int, error: String?) {
                    it.resumeWithException(IMError(code, error))
                }

                override fun onSuccess(s: Message?) {
                    it.resume(s)
                }

                override fun onProgress(progress: Long) {

                }
            }, imClient.messageManager.createTextMessage(content), address, null, OfflinePushInfo()
        )
    }
}

data class IMError(val code: Int, override val message: String?) : Exception()

