package com.ti4n.freechat.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.network.EthScanApiService
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.network.FreeChatIMService
import com.ti4n.freechat.network.SwapApiService
import com.ti4n.freechat.network.ethScanUrl
import com.ti4n.freechat.network.freeChatIMUrl
import com.ti4n.freechat.network.freeChatUrl
import com.ti4n.freechat.network.swapBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

val Context.dataStore by preferencesDataStore(name = "account")

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
    fun provideFreeChatApi(okHttpClient: OkHttpClient): FreeChatApiService {
        return Retrofit.Builder().baseUrl(freeChatUrl).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build().create()
    }

    @Provides
    fun provideEthScanApi(okHttpClient: OkHttpClient): EthScanApiService {
        return Retrofit.Builder().baseUrl(ethScanUrl).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build().create()
    }

    @Provides
    fun provideSwapApi(okHttpClient: OkHttpClient): SwapApiService {
        return Retrofit.Builder().baseUrl(swapBaseUrl).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build().create()
    }

    @Provides
    fun provideIMApi(okHttpClient: OkHttpClient): FreeChatIMService {
        return Retrofit.Builder().baseUrl(freeChatIMUrl).client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create()).build().create()
    }

    @Provides
    fun provideAppDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDataBase::class.java, "app.db").build()
}