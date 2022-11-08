package com.ti4n.freechat.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ti4n.freechat.util.IM
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.openim.android.sdk.enums.MessageType
import io.openim.android.sdk.models.Message

class MessagePagingSource @AssistedInject constructor(
    @Assisted("userId") val userId: String, @Assisted("conversationId") val conversationId: String
) : PagingSource<Message, Message>() {
    override suspend fun load(params: LoadParams<Message>): LoadResult<Message, Message> {
        val messages = mutableListOf<Message>()
        return try {
            messages.addAll(IM.getHistoryMessages(params.key, userId, conversationId).reversed())
            LoadResult.Page(
                data = messages,
                prevKey = null,
                nextKey = if (messages.count() < 20) null else messages.lastOrNull()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        } finally {
            IM.markMessagesRead(userId,
                messages.filter { it.sendID == userId && (it.contentType == MessageType.TEXT || it.contentType == MessageType.VOICE || it.contentType == MessageType.PICTURE || it.contentType == 10000) && !it.isRead }
                    .map { it.clientMsgID })
        }
    }

    override fun getRefreshKey(state: PagingState<Message, Message>) = null
}

@AssistedFactory
interface MessagePagingSourceFactory {
    fun create(
        @Assisted("userId") userId: String, @Assisted("conversationId") conversationId: String
    ): MessagePagingSource
}