package com.steingolditay.app.matrixapp.di

import com.steingolditay.app.matrixapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RetrofitModule {

    @Singleton
    @Provides
    fun providesRetrofit(): RetrofitInterface {
        return   Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.apiBaseUrl)
                .build()
                .create(RetrofitInterface::class.java)

    }



}