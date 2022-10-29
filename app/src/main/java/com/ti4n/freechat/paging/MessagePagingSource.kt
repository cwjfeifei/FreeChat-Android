package com.ti4n.freechat.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ti4n.freechat.util.IM
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import io.openim.android.sdk.models.Message

class MessagePagingSource @AssistedInject constructor(@Assisted val userId: String) :
    PagingSource<Message, Message>() {
    override suspend fun load(params: LoadParams<Message>): LoadResult<Message, Message> {
        return try {
            val messages = IM.getHistoryMessages(params.key, userId)
            LoadResult.Page(
                data = messages,
                prevKey = null,
                nextKey = if (messages.count() < 20) null else messages.lastOrNull()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Message, Message>) = null
}

@AssistedFactory
interface MessagePagingSourceFactory {
    fun create(
        @Assisted userId: String
    ): MessagePagingSource
}