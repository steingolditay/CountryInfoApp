package com.steingolditay.app.matrixapp.di

import com.steingolditay.app.matrixapp.model.CountryItem
import retrofit2.http.GET

interface RetrofitInterface {

    @GET("all")
    suspend fun getAllCountries(): List<CountryItem>?

}