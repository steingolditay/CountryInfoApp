package com.steingolditay.app.matrixapp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.steingolditay.app.matrixapp.model.CountryItem
import com.steingolditay.app.matrixapp.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountryListViewModel

@Inject constructor (private val repository: Repository): ViewModel() {
    private val _countriesData = MutableLiveData<List<CountryItem>>()
    val countriesData: LiveData<List<CountryItem>> = _countriesData

    fun getAllCountries(){
        viewModelScope.launch(Dispatchers.IO) {
            _countriesData.postValue(repository.getAllCountries())
        }
    }
}