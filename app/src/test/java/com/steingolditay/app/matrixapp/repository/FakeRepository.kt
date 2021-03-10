package com.steingolditay.app.matrixapp.repository

import com.steingolditay.app.matrixapp.di.RetrofitInterface
import com.steingolditay.app.matrixapp.model.CountryItem

class FakeRepository: RetrofitInterface {

    // check for retrofit instance responses
    // returns null in case of network error
    // return list of items in case of successful response
    private var shouldReturnNull = false
    override suspend fun getAllCountries(): List<CountryItem>? {
        val fakeItem = CountryItem("name", "nativeName", "nam", 0f, listOf(), "someUrl")
        return if (shouldReturnNull){
            null
        }
        else {
             listOf(fakeItem)
        }
    }
}