package com.ti4n.freechat.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.ti4n.freechat.erc20.ERC20Tokens
import com.ti4n.freechat.network.EthScanApiService
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.network.ethScanUrl
import com.ti4n.freechat.network.freeChatUrl
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
import java.io.IOException

typealias PreferencesDataStore = DataStore<Preferences>

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
        return Retrofit.Builder().baseUrl(freeChatUrl)
            .addConverterFactory(GsonConverterFactory.create()).build().create()
    }

    @Provides
    fun provideEthScanApi(okHttpClient: OkHttpClient): EthScanApiService {
        return Retrofit.Builder().baseUrl(ethScanUrl)
            .addConverterFactory(GsonConverterFactory.create()).build().create()
    }

//    @Provides
//    fun provideAppDb(@ApplicationContext context: Context) =
//        Room.databaseBuilder(context, AppDataBase::class.java, "app.db").build()

    @Provides
    fun provideErc20Tokens(@ApplicationContext context: Context): ERC20Tokens {
        try {
            val `is` = context.assets.open("TokenListInfos.json")
            val size: Int = `is`.available()
            val buffer = ByteArray(size)
            `is`.read(buffer)
            `is`.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            return Gson().fromJson(jsonString, ERC20Tokens::class.java)
        } catch (e: IOException) {
            e.printStackTrace()
            return ERC20Tokens(emptyList())
        }
    }
}