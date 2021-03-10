package com.steingolditay.app.matrixapp.repository

import com.steingolditay.app.matrixapp.di.RetrofitInterface
import com.steingolditay.app.matrixapp.model.CountryItem
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.lang.Exception
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class Repository

@Inject constructor(private val retrofit: RetrofitInterface) : RetrofitInterface {

    override suspend fun getAllCountries(): List<CountryItem>? {
        return try {
            retrofit.getAllCountries()
        } catch (e: Exception) {
            null
        }
    }

}