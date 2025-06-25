package com.sigmas.dogapp.di
import com.sigmas.dogapp.Network.DogApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder().baseUrl("https://dog.ceo/api/").addConverterFactory(GsonConverterFactory.create()).build()

    @Provides
    fun provideDogApiService(retrofit: Retrofit): DogApiService = retrofit.create(DogApiService::class.java)
}