package com.ti4n.freechat.network

import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.erc20.ERC20Tokens
import com.ti4n.freechat.model.request.Register
import com.ti4n.freechat.model.response.BaseResponse
import com.ti4n.freechat.model.response.FaceImageInfo
import com.ti4n.freechat.model.response.UserToken
import com.ti4n.freechat.model.response.freechat.FreeChatBaseResponse
import com.ti4n.freechat.model.response.freechat.Rate
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query
import retrofit2.http.Url


const val freeChatUrl = "https://freechat.world/api/"

interface FreeChatApiService {

    @GET("v5/market/tokenlist")
    suspend fun getSupportTokens(): ERC20Tokens

    @GET("v5/market/index-tickers")
    suspend fun getRate(@Query("instId") instId: String): FreeChatBaseResponse<List<Rate>>

    @POST()
    suspend fun getAvatars(@Url url: String = "http://8.218.80.2:10008/user/select_face"): BaseResponse<List<FaceImageInfo>>
}


