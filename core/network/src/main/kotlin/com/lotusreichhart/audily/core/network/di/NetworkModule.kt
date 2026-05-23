package com.lotusreichhart.audily.core.network.di

import com.lotusreichhart.audily.core.network.service.LrcLibService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.lotusreichhart.audily.core.network.qualifier.LrcLibRetrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(okHttpClient: OkHttpClient): Retrofit.Builder {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
    }

    @Provides
    @Singleton
    @LrcLibRetrofit
    fun provideLrcLibRetrofit(retrofitBuilder: Retrofit.Builder): Retrofit {
        return retrofitBuilder
            .baseUrl("https://lrclib.net/")
            .build()
    }

    @Provides
    @Singleton
    fun provideLrcLibService(@LrcLibRetrofit retrofit: Retrofit): LrcLibService {
        return retrofit.create(LrcLibService::class.java)
    }
}
