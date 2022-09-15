package com.ti4n.freechat.network

import com.ti4n.freechat.model.Value
import com.ti4n.freechat.model.response.CreatedAddress
import com.ti4n.freechat.model.response.GeneratedAddress
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body


const val tronBaseUrl = "https://api.shasta.trongrid.io/"

interface TronApiService {

    @POST("wallet/createaddress")
    suspend fun createAddress(@Body value: Value): CreatedAddress

    @GET("wallet/generateaddress")
    suspend fun generateAddress(): GeneratedAddress
}