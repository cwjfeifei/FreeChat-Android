package com.ti4n.freechat.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ti4n.freechat.model.response.freechat.ethereum
import com.ti4n.freechat.model.response.Transaction
import com.ti4n.freechat.network.EthScanApiService
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class EthTransactionPagingSource @AssistedInject constructor(
    val ethScanApiService: EthScanApiService,
    @Assisted("address") val address: String,
    @Assisted("tokenAddress") val tokenAddress: String? = null,
) :
    PagingSource<Int, Transaction>() {
    override fun getRefreshKey(state: PagingState<Int, Transaction>) = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Transaction> {
        val page = params.key ?: 1
        return try {
            val transactions = if (tokenAddress == ethereum.contractAddress) {
                ethScanApiService.ethTractionHistory(address, page)
            } else {
                ethScanApiService.tokenTractionHistory(address, page, tokenAddress!!)
            }
            LoadResult.Page(
                transactions.result,
                null,
                if (transactions.result.count() < 20) null else page + 1
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
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