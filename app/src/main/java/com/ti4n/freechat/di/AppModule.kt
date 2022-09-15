package com.ti4n.freechat.di

import com.ti4n.freechat.network.TronApiService
import com.ti4n.freechat.network.tronBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideOkhttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder().addInterceptor(logging).build()
    }

    @Provides
    fun provideTronApi(okHttpClient: OkHttpClient): TronApiService {
        return Retrofit.Builder().baseUrl(tronBaseUrl)
            .addConverterFactory(GsonConverterFactory.create()).build().create()
    }
}