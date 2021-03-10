package com.steingolditay.app.matrixapp.repository

import com.steingolditay.app.matrixapp.model.CountryItem

interface RepositoryInterface {

    suspend fun getAllCountries(): List<CountryItem>?

}