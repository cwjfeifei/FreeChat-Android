package com.ti4n.freechat.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
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
import io.openim.android.sdk.models.FriendshipInfo
import io.openim.android.sdk.models.Message
import io.openim.android.sdk.models.OfflinePushInfo
import io.openim.android.sdk.models.ReadReceiptInfo
import io.openim.android.sdk.models.RevokedInfo
import io.openim.android.sdk.models.SoundElem
import io.openim.android.sdk.models.UserInfo
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import open_im_sdk.Open_im_sdk
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object IM {

    val imClient = OpenIMClient.getInstance()
    val currentUserInfo = MutableStateFlow(UserInfo())
    val newMessages = mutableStateListOf<Message>()
    val conversations = mutableStateListOf<ConversationInfo>()
    val friends = mutableStateListOf<FriendInfo>()
    val totalUnreadCount = MutableStateFlow(0)
    val showNewFriendApplication = MutableStateFlow(false)

    fun init(context: Context) {
        imClient.initSDK(7,
            "http://47.57.185.242:10002",
            "ws://47.57.185.242:10001",
            context.cacheDir.absolutePath,
            6,
            "aws",
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
                it.resume(data)
                getAllConversations()
                getFriend()
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
                    newMessages.add(msg)
                }
            }

            override fun onRecvC2CReadReceipt(list: MutableList<ReadReceiptInfo>?) {
                TODO("Not yet implemented")
            }

            override fun onRecvGroupMessageReadReceipt(list: MutableList<ReadReceiptInfo>?) {
            }

            override fun onRecvMessageRevoked(msgId: String?) {

            }

            override fun onRecvMessageRevokedV2(info: RevokedInfo?) {
                if (info != null) {
                    newMessages.removeAll { it.clientMsgID == info.clientMsgID }
                }
            }
        })
        imClient.friendshipManager.setOnFriendshipListener(object : OnFriendshipListener {
            override fun onBlacklistAdded(u: BlacklistInfo?) {
            }

            override fun onBlacklistDeleted(u: BlacklistInfo?) {
            }

            override fun onFriendApplicationAccepted(u: FriendApplicationInfo?) {
            }

            override fun onFriendApplicationAdded(u: FriendApplicationInfo?) {
                showNewFriendApplication.value = true
            }

            override fun onFriendApplicationDeleted(u: FriendApplicationInfo?) {
            }

            override fun onFriendApplicationRejected(u: FriendApplicationInfo?) {
            }

            override fun onFriendInfoChanged(u: FriendInfo?) {
                u?.let { friend ->
                    friends.removeAll { it.userID == friend.userID }
                    friends.add(friend)
                }
            }

            override fun onFriendAdded(u: FriendInfo?) {
                u?.let {
                    friends.add(it)
                }
            }

            override fun onFriendDeleted(u: FriendInfo?) {
                friends.removeAll { u?.userID == it.userID }
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

    suspend fun getHistoryMessages(startMsg: Message?, userId: String) = suspendCoroutine {
        imClient.messageManager.getHistoryMessageListReverse(
            object : OnBase<List<Message>> {
                override fun onError(code: Int, error: String?) {
                    it.resume(listOf<Message>())
                }

                override fun onSuccess(data: List<Message>?) {
                    it.resume(data ?: listOf())
                }

            }, userId, null, "single_$userId", startMsg, 20
        )
    }

    fun getAllConversations() {
        imClient.conversationManager.getAllConversationList(object :
            OnBase<List<ConversationInfo>> {
            override fun onError(code: Int, error: String?) {
                Log.e("get conversations", "onError: $error")
            }

            override fun onSuccess(data: List<ConversationInfo>?) {
                data?.let {
                    conversations.clear()
                    conversations.addAll(it)
                }
            }

        })
    }

    suspend fun sendTextMessage(address: String, content: String) = suspendCoroutine {
        imClient.conversationManager.getOneConversation(object : OnBase<ConversationInfo> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: ConversationInfo?) {
                imClient.messageManager.sendMessage(
                    object : OnMsgSendCallback {
                        override fun onError(code: Int, error: String?) {
                            it.resumeWithException(IMError(code, error))
                        }

                        override fun onSuccess(s: Message?) {
                            it.resume(s)
                            if (s != null) newMessages.add(s)
                        }

                        override fun onProgress(progress: Long) {

                        }
                    },
                    imClient.messageManager.createTextMessage(content),
                    address,
                    null,
                    OfflinePushInfo()
                )
            }
        }, address, 1)
    }

    suspend fun sendImageMessage(address: String, imagePath: String) = suspendCoroutine {
        imClient.conversationManager.getOneConversation(object : OnBase<ConversationInfo> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: ConversationInfo?) {
                imClient.messageManager.sendMessage(
                    object : OnMsgSendCallback {
                        override fun onError(code: Int, error: String?) {
                            it.resumeWithException(IMError(code, error))
                        }

                        override fun onSuccess(s: Message?) {
                            it.resume(s)
                            if (s != null) newMessages.add(s)
                        }

                        override fun onProgress(progress: Long) {

                        }
                    },
                    imClient.messageManager.createImageMessageFromFullPath(imagePath),
                    address,
                    null,
                    OfflinePushInfo()
                )
            }
        }, address, 1)
    }

    suspend fun sendImageMessage(context: Context, address: String, uri: Uri): Message? {
        context.getPathFromUri(uri)?.let {
            return sendImageMessage(address, it)
        }
        return null
    }

    suspend fun sendVoiceMessage(address: String, voicePath: String, duration: Long) =
        suspendCoroutine {
            imClient.messageManager.sendMessage(
                object : OnMsgSendCallback {
                    override fun onError(code: Int, error: String?) {
                        it.resumeWithException(IMError(code, error))
                    }

                    override fun onSuccess(s: Message?) {
                        it.resume(s)
                        if (s != null) newMessages.add(s)
                    }

                    override fun onProgress(progress: Long) {

                    }
                },
                imClient.messageManager.createSoundMessageFromFullPath(voicePath, duration),
                address, null, OfflinePushInfo()
            )
        }

    suspend fun getUserInfo(vararg toUserId: String) = suspendCoroutine {
        imClient.userInfoManager.getUsersInfo(object : OnBase<List<UserInfo>> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: List<UserInfo>?) {
                it.resume(data)
            }
        }, toUserId.asList())
    }

    suspend fun pinConversation(conversationId: String, isPin: Boolean) = suspendCoroutine {
        imClient.conversationManager.pinConversation(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: String?) {
                it.resume(data)
                conversations[conversations.indexOfFirst { it.conversationID == conversationId }] =
                    conversations.first { it.conversationID == conversationId }.apply {
                        isPinned = isPin
                    }
            }
        }, conversationId, isPin)
    }

    suspend fun deleteConversation(conversationId: String) = suspendCoroutine {
        imClient.conversationManager.deleteConversation(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: String?) {
                it.resume(data)
                conversations.removeAll { it.conversationID == conversationId }
            }
        }, conversationId)
    }

    fun getFriend() {
        imClient.friendshipManager.getFriendList(object : OnBase<List<UserInfo>> {
            override fun onError(code: Int, error: String?) {
                Log.e("friend list", "onError: $error")
            }

            override fun onSuccess(data: List<UserInfo>?) {
                data?.let {
                    friends.addAll(it.filter { it.isFriendship }.map { it.friendInfo })
                }
            }
        })
    }

    suspend fun isFriend(with: String) = suspendCoroutine {
        imClient.friendshipManager.checkFriend(object : OnBase<List<FriendshipInfo>> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: List<FriendshipInfo>?) {
                it.resume(data?.firstOrNull()?.result == 1)
            }
        }, listOf(with))
    }

    suspend fun addFriend(id: String) = suspendCoroutine {
        imClient.friendshipManager.addFriend(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: String?) {
                it.resume(data)
            }
        }, id, "")
    }

    suspend fun setRemark(id: String, remark: String) = suspendCoroutine {
        imClient.friendshipManager.setFriendRemark(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: String?) {
                it.resume(data)
            }
        }, id, remark)
    }

    suspend fun getFriendApplication() = suspendCoroutine {
        imClient.friendshipManager.getRecvFriendApplicationList(object :
            OnBase<List<FriendApplicationInfo>> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: List<FriendApplicationInfo>?) {
                it.resume(data)
            }
        })
    }
}

data class IMError(val code: Int, override val message: String?) : Exception()

