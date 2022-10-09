package com.ti4n.freechat.network

import com.ti4n.freechat.model.response.EthScanBaseResponse
import com.ti4n.freechat.model.response.Transaction
import retrofit2.http.GET
import retrofit2.http.Query

const val ethScanUrl = "https://api.etherscan.io/"

interface EthScanApiService {

    @GET("api?module=account&action=txlist&startblock=0&endblock=99999999&offset=20&sort=asc&apikey=FZTI57USSADTZ2IZI6TSFY1T98S1IU492M")
    suspend fun ethTractionHistory(
        @Query("address") address: String,
        @Query("page") page: Int
    ): EthScanBaseResponse<List<Transaction>>

    @GET("api?module=account&action=tokentx&startblock=0&endblock=99999999&offset=20&sort=asc&apikey=FZTI57USSADTZ2IZI6TSFY1T98S1IU492M")
    suspend fun tokenTractionHistory(
        @Query("address") address: String,
        @Query("page") page: Int,
        @Query("contractAddress") contractAddress: String
    ): EthScanBaseResponse<List<Transaction>>
}