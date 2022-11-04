package com.ti4n.freechat.network

import com.ti4n.freechat.model.request.GetSelfInfo
import com.ti4n.freechat.model.request.GetToken
import com.ti4n.freechat.model.request.Register
import com.ti4n.freechat.model.response.BaseResponse
import com.ti4n.freechat.model.response.UserToken
import com.ti4n.freechat.model.response.freechat.SelfInfo
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

const val freeChatIMUrl = "http://47.57.185.242:10002/"

interface FreeChatIMService {

    @POST("auth/user_register")
    suspend fun register(@Body register: Register): BaseResponse<UserToken>

    @POST("auth/user_token")
    suspend fun getToken(@Body getToken: GetToken): BaseResponse<UserToken>

    @POST("user/get_self_user_info")
    suspend fun getSelfInfo(
        @Body getSelfInfo: GetSelfInfo,
        @Header("token") token: String
    ): BaseResponse<SelfInfo>
}