package com.ti4n.freechat.util

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.ti4n.freechat.model.im.BaseInfo
import com.ti4n.freechat.model.im.IFriendInfo
import com.ti4n.freechat.model.im.IUserInfo
import com.ti4n.freechat.model.im.toBaseInfo
import com.ti4n.freechat.model.im.toIFriendInfo
import com.ti4n.freechat.model.im.toIUserInfo
import io.openim.android.sdk.OpenIMClient
import io.openim.android.sdk.enums.Platform
import io.openim.android.sdk.listener.*
import io.openim.android.sdk.models.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val TAG = "IM"

object IM {

    val imClient = OpenIMClient.getInstance()
    val currentUserInfo = MutableStateFlow<BaseInfo?>(null)
    val newMessages = mutableStateListOf<Message>()
    val conversations = mutableStateListOf<ConversationInfo>()
    val friends = MutableStateFlow(emptyList<BaseInfo>())
    val totalUnreadCount = MutableStateFlow(0)
    val showNewFriendApplication = MutableStateFlow(false)

    val DEFAULT_FACEURL = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png" // TODO

    fun init(context: Context) {
        imClient.initSDK(Platform.ANDROID,
            "http://imtest.freechat.world:10002",
            "ws://imtest.freechat.world:10001",
            context.filesDir.absolutePath,
            Log.DEBUG,
            "minio",
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
                getSelfInfo()
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

    /**
     * don't need to call this, @see setOnUserListener
     */
    fun getSelfInfo() {
        imClient.userInfoManager.getSelfUserInfo(object : OnBase<UserInfo> {
            override fun onError(code: Int, error: String?) {
            }

            override fun onSuccess(data: UserInfo?) {
                data?.let {
                    currentUserInfo.value = it.toBaseInfo()
                }
            }
        })
    }

    private fun setListener() {
        imClient.userInfoManager.setOnUserListener {
            currentUserInfo.value = it.toBaseInfo()
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
                    val pre = friends.value.toMutableList()
                    pre.removeAll { it.userID == friend.userID }
                    pre.add(friend.toBaseInfo())
                    friends.value = pre
                }
            }

            override fun onFriendAdded(u: FriendInfo?) {
                u?.let { friend ->
                    val pre = friends.value.toMutableList()
                    pre.removeAll { it.userID == friend.userID }
                    pre.add(friend.toBaseInfo())
                    friends.value = pre
                }
            }

            override fun onFriendDeleted(u: FriendInfo?) {
                val pre = friends.value.toMutableList()
                pre.removeAll { it.userID == u?.userID }
                friends.value = pre
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

    /**
     * gender : 1-male, 2-female
     * appManagerLevel : 1-normal user, 2-admin user
     */
    suspend fun setUserInfo(
        faceURL: String?,
        nickname: String?,
        gender: Int,
        birth: Long,
        email: String?,
        ex: String?
    ) =
        suspendCoroutine {
            imClient.userInfoManager.setSelfInfo(
                object : OnBase<String> {
                    override fun onError(code: Int, error: String?) {
                        it.resume(IMError(code, error))
                    }

                    override fun onSuccess(data: String?) {
                        it.resume(Unit)
                    }
                }, nickname, faceURL, gender, 1, "", birth, email, ex
            )
        }

    suspend fun getHistoryMessages(startMsg: Message?, userId: String, conversationId: String) =
        suspendCoroutine {
            imClient.messageManager.getHistoryMessageList(
                object : OnBase<List<Message>> {
                    override fun onError(code: Int, error: String?) {
                        it.resume(listOf<Message>())
                    }

                    override fun onSuccess(data: List<Message>?) {
                        it.resume(data ?: listOf())
                    }

                }, userId, null, conversationId, startMsg, 20
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

    suspend fun markMessagesRead(userId: String, messages: List<String>) = suspendCoroutine {
        imClient.messageManager.markC2CMessageAsRead(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: String?) {
                it.resume(data)
            }
        }, userId, messages)
    }

    fun getConversationId(toUserId: String) =
        imClient.conversationManager.getConversationIDBySessionType(toUserId, 0)

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

    data class TransferMessageContent(
        val tokenIcon: String,
        val amount: String,
        val txHash: String
    )

    suspend fun sendRedPackMessage(
        toUserId: String,
        tokenIcon: String,
        amount: String,
        txHash: String
    ) =
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
                imClient.messageManager.createCustomMessage(
                    Gson().toJson(
                        TransferMessageContent(
                            tokenIcon,
                            amount,
                            txHash
                        )
                    ), "transfer", ""
                ),
                toUserId,
                null,
                OfflinePushInfo()
            )
        }

    suspend fun getUserInfo(toUserId: String) = suspendCoroutine {
        imClient.userInfoManager.getUsersInfo(object : OnBase<List<UserInfo>> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: List<UserInfo>) {
                val info = data.firstOrNull()
                it.resume(info)
            }
        }, listOf(toUserId))
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
                    val pre = friends.value.toMutableList()
                    pre.addAll(it.filter { it.isFriendship }.map { it.friendInfo.toBaseInfo() })
                    friends.value = pre
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

    suspend fun addFriend(id: String, requestInfo: String) = suspendCoroutine {
        imClient.friendshipManager.addFriend(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: String?) {
                it.resume(data)
            }
        }, id, requestInfo)
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

    suspend fun acceptFriendApplication(id: String) = suspendCoroutine {
        imClient.friendshipManager.acceptFriendApplication(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: String?) {
                it.resume(data)
            }
        }, id, "")
    }

    suspend fun rejectFriendApplication(id: String) = suspendCoroutine {
        imClient.friendshipManager.refuseFriendApplication(object : OnBase<String> {
            override fun onError(code: Int, error: String?) {
                it.resumeWithException(IMError(code, error))
            }

            override fun onSuccess(data: String?) {
                Log.e(TAG, "onSuccess: $id $data")
                it.resume(data)
            }
        }, id, "")
    }
}

data class IMError(val code: Int, override val message: String?) : Exception()

