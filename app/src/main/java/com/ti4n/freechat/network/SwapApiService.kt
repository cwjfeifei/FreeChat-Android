package com.ti4n.freechat.network

import com.ti4n.freechat.erc20.ERC20Tokens
import com.ti4n.freechat.model.response.swap.Allowance
import com.ti4n.freechat.model.response.swap.Quote
import com.ti4n.freechat.model.response.swap.SwapResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url


const val swapBaseUrl = "https://api.1inch.io/v4.0/1/"

interface SwapApiService {

    @GET("approve/allowance")
    suspend fun getAllowedNumber(
        @Query("tokenAddress") tokenAddress: String,
        @Query("walletAddress") walletAddress: String
    ): Allowance

    @GET("quote")
    suspend fun quote(
        @Query("fromTokenAddress") fromTokenAddress: String,
        @Query("toTokenAddress") toTokenAddress: String,
        @Query("amount") amount: String
    ): Quote

    @GET("swap")
    suspend fun swap(
        @Query("fromTokenAddress") fromTokenAddress: String,
        @Query("toTokenAddress") toTokenAddress: String,
        @Query("amount") amount: String,
        @Query("fromAddress") fromAddress: String,
        @Query("slippage") slippage: Double
    ): SwapResponse
}