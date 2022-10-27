package com.ti4n.freechat.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ti4n.freechat.util.IM
import io.openim.android.sdk.models.Message

class MessagePagingSource : PagingSource<Message, Message>() {
    override suspend fun load(params: LoadParams<Message>): LoadResult<Message, Message> {
        return try {
            val messages = IM.getHistoryMessages(params.key, "")
            LoadResult.Page(data = messages, prevKey = null, nextKey = messages.firstOrNull())
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Message, Message>) = null
}