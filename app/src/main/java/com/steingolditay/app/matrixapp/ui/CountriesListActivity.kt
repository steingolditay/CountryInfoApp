package com.steingolditay.app.matrixapp.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.steingolditay.app.matrixapp.R
import com.steingolditay.app.matrixapp.databinding.ActivityAllCountryListBinding
import com.steingolditay.app.matrixapp.model.CountryItem
import com.steingolditay.app.matrixapp.utils.Constants
import com.steingolditay.app.matrixapp.utils.NetworkConnectionMonitor
import com.steingolditay.app.matrixapp.viewmodels.CountryListViewModel
import com.steingolditay.app.matrixapp.ui.adapters.CountryListAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CountriesListActivity : AppCompatActivity(), CountryListAdapter.OnItemClickListener {

    private lateinit var binding: ActivityAllCountryListBinding
    private lateinit var viewModel: CountryListViewModel
    private lateinit var adapter: CountryListAdapter

    private var countriesMap = HashMap<String, CountryItem>()

    private var errorDialogIsShowing: Boolean = false
    private var connectedToInternet: Boolean = true

    @Inject lateinit var networkConnectionMonitor: NetworkConnectionMonitor
    private var sortByNameState = Constants.descending
    private var sortBySizeState = Constants.descending


    private lateinit var arrowUpDrawable: Drawable
    private lateinit var arrowDownDrawable: Drawable



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllCountryListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        arrowUpDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.arrow_up)!!
        arrowDownDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.arrow_down)!!

        viewModel = ViewModelProvider(this).get(CountryListViewModel::class.java)


        binding.sortByNameButton.setOnClickListener{
            sortCountryListByName()
        }

        binding.sortBySizeButton.setOnClickListener {
            sortCountryListBySize()
        }

        initNetworkConnectionMonitor()

    }

    // Watch for internet connectivity changes
    // if no connection is found, present an error
    // if connected, init view model
    private fun initNetworkConnectionMonitor(){
        networkConnectionMonitor.registerNetworkCallback()
        networkConnectionMonitor.liveData.observe(this, {
            connectedToInternet = it
            if (it){
                showProgressBar()
                updateUiConnected()
                initViewModel()
            }
            else {
                updateUiDisconnected()
            }
        })
    }

    // fetch data
    private fun initViewModel() {
        viewModel.getAllCountries()
        viewModel.countriesData.observe(this, { list ->
            hideProgressBar()
            if (list != null) {
                initRecyclerView(list)
                for (country in list) {
                    countriesMap[country.alpha3Code!!] = country
                }

            } else {
                if (!errorDialogIsShowing && !connectedToInternet) {
                    errorDialogIsShowing = true
                    showDataFetchAlertDialog()
                }
            }
        })
    }

    // load items to recycler view
    private fun initRecyclerView(countryList: List<CountryItem>) {
        adapter = CountryListAdapter(this, countryList, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    private fun updateUiConnected() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.sortByNameButton.visibility = View.VISIBLE
        binding.sortBySizeButton.visibility = View.VISIBLE

        binding.connectionLost.visibility = View.GONE
    }

    private fun updateUiDisconnected() {
        binding.recyclerView.visibility = View.GONE
        binding.sortByNameButton.visibility = View.GONE
        setSortByNameDrawable(null)
        binding.sortBySizeButton.visibility = View.GONE
        setSortBySizeDrawable(null)

        binding.connectionLost.visibility = View.VISIBLE
    }

    // shows up if retrofit response is null
    private fun showDataFetchAlertDialog() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setMessage(getString(R.string.service_unavailable))
        alertBuilder.setCancelable(false)

        alertBuilder.setPositiveButton(getString(R.string.retry)){ dialog, _ ->
            dialog?.dismiss()
            errorDialogIsShowing = false
            viewModel.getAllCountries()
        }
        val dialog = alertBuilder.create()

        dialog.show()
    }

    // on recyclerview item click
    // move user to the respective country activity
    // and pass the border counties items
    override fun onItemClick(countryItem: CountryItem) {
        val borderCountries = ArrayList<CountryItem>()

        for (border in countryItem.borders!!){
            borderCountries.add(countriesMap[border]!!)
        }

        val intent = Intent(this, CountryBordersListActivity::class.java)
        intent.putExtra(Constants.countryItem, countryItem)
        intent.putExtra(Constants.borderCountriesItems, borderCountries)
        startActivity(intent)
    }

    private fun sortCountryListByName(){
        setSortBySizeDrawable(null)
        val sortedMap: Map<String, CountryItem>

        val listData = viewModel.countriesData.value
        if (listData != null){
            if (sortByNameState == Constants.ascending){
                sortedMap = listData.sortedByDescending { it.name }.map { it.alpha3Code!! to it }.toMap()
                sortByNameState = Constants.descending
                setSortByNameDrawable(arrowDownDrawable)

            }
            else {
                sortedMap = listData.sortedBy { it.name }.map { it.alpha3Code!! to it }.toMap()
                sortByNameState = Constants.ascending
                setSortByNameDrawable(arrowUpDrawable)
            }
            countriesMap = HashMap(sortedMap)
            initRecyclerView(sortedMap.values.toList())
        }
    }

    private fun sortCountryListBySize(){
        setSortByNameDrawable(null)
        val sortedMap: Map<String, CountryItem>

        val listData = viewModel.countriesData.value
        if (listData != null) {
            if (sortBySizeState == Constants.ascending){
                sortedMap = listData.sortedByDescending { it.area }.map { it.alpha3Code!! to it }.toMap()
                sortBySizeState = Constants.descending
                setSortBySizeDrawable(arrowDownDrawable)
            }
            else {
                sortedMap = listData.sortedBy { it.area }.map { it.alpha3Code!! to it }.toMap()
                sortBySizeState = Constants.ascending
                setSortBySizeDrawable(arrowUpDrawable)
            }
            countriesMap = HashMap(sortedMap)

            initRecyclerView(sortedMap.values.toList())

        }
    }

    private fun showProgressBar(){ binding.progressBar.visibility = View.VISIBLE }

    private fun hideProgressBar(){ binding.progressBar.visibility = View.GONE }

    private fun setSortByNameDrawable(drawable: Drawable?){
        when (drawable) {
            null -> {
                binding.sortByNameButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            else -> {
                binding.sortByNameButton.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

            }
        }
    }

    private fun setSortBySizeDrawable(drawable: Drawable?){
        when (drawable) {
            null -> {
                binding.sortBySizeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }
            else -> {
                binding.sortBySizeButton.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)

            }
        }
    }


}