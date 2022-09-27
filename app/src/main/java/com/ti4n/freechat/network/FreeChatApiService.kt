package com.ti4n.freechat.network

import com.ti4n.freechat.model.request.Register
import com.ti4n.freechat.model.response.BaseResponse
import com.ti4n.freechat.model.response.UserToken
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body


const val freeChatUrl = "http://47.57.185.242:10002/"

interface FreeChatApiService {

    @POST("auth/user_register")
    suspend fun register(@Body register: Register): BaseResponse<UserToken>
}