package com.ti4n.freechat.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ti4n.freechat.model.response.Transaction
import com.ti4n.freechat.network.EthScanApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import javax.inject.Inject

class EthTransactionPagingSource @AssistedInject constructor(
    val ethScanApiService: EthScanApiService,
    @Assisted("address") val address: String,
    @Assisted("tokenAddress") val tokenAddress: String? = null,
) :
    PagingSource<Int, Transaction>() {
    override fun getRefreshKey(state: PagingState<Int, Transaction>) = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        val page = params.key ?: 1
        try {
            val transactions = if (tokenAddress == null) {
                ethScanApiService.ethTractionHistory(address, page)
            } else {
                ethScanApiService.tokenTractionHistory(address, page, tokenAddress)
            }
            return LoadResult.Page(transactions.result, null, page + 1)
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}

@AssistedFactory
interface EthTransactionPagingSourceFactory {
    fun create(
        @Assisted("address") address: String,
        @Assisted("tokenAddress") tokenAddress: String? = null
    ): EthTransactionPagingSource
}