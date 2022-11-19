package com.ti4n.freechat.network

import com.ti4n.freechat.model.request.Login
import com.ti4n.freechat.model.request.Register
import com.ti4n.freechat.model.response.BaseResponse
import com.ti4n.freechat.model.response.UserToken
import com.ti4n.freechat.model.response.freechat.SelfInfo
import com.ti4n.freechat.model.request.SendVerifyCode
import com.ti4n.freechat.model.response.freechat.Account
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

const val freeChatIMUrl = "http://imtest.freechat.world:10004/"

interface FreeChatIMService {

    @POST("auth/password")
    suspend fun register(@Body register: Register): BaseResponse<UserToken>

    @POST("auth/code")
    suspend fun sendVerifyCode(@Body sendVerifyCode: SendVerifyCode): BaseResponse<Account>

    @POST("auth/login")
    suspend fun login(@Body login: Login): BaseResponse<UserToken>
}