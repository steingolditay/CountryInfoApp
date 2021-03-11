package com.steingolditay.app.matrixapp.ui

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
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

@AndroidEntryPoint
class CountriesListActivity : AppCompatActivity(), CountryListAdapter.OnItemClickListener {

    private lateinit var binding: ActivityAllCountryListBinding
    private lateinit var viewModel: CountryListViewModel
    private lateinit var adapter: CountryListAdapter

    private var countriesMap = HashMap<String, CountryItem>()

    private var errorDialogIsShowing: Boolean = false
    private var connectedToInternet: Boolean = true

    private val networkConnectionMonitor = NetworkConnectionMonitor(this)
    private var sortByNameState = Constants.descending
    private var sortBySizeState = Constants.descending


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllCountryListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(CountryListViewModel::class.java)


        binding.sortByName.setOnClickListener{
            sortCountryListByName()
        }

        binding.sortBySize.setOnClickListener {
            sortCountryListBySize()
        }


        // Watch for internet connectivity changes
        // if no connection is found, present an error
        networkConnectionMonitor.registerNetworkCallback()
        networkConnectionMonitor.liveData.observe(this, {
            connectedToInternet = it
            if (it){
                showProgressBar()
                updateUIConnected()
                initViewModel()
            }
            else {
                updateUIDisconnected()
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

    private fun updateUIConnected() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.sortByName.visibility = View.VISIBLE
        binding.sortBySize.visibility = View.VISIBLE

        binding.connectionLost.visibility = View.GONE
    }

    private fun updateUIDisconnected() {
        binding.recyclerView.visibility = View.GONE
        binding.sortByName.visibility = View.GONE
        binding.sortByNameArrow.visibility = View.GONE
        binding.sortBySize.visibility = View.GONE
        binding.sortBySizeArrow.visibility = View.GONE

        binding.connectionLost.visibility = View.VISIBLE
    }

    private fun showDataFetchAlertDialog() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setMessage(getString(R.string.service_unavailable))
        alertBuilder.setCancelable(false)

        alertBuilder.setPositiveButton(getString(R.string.retry),
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.dismiss()
                    errorDialogIsShowing = false
                    viewModel.getAllCountries()
                }
            })
        val dialog = alertBuilder.create()

        dialog.show()
    }

    // on item click move user to the respective
    // country activity
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
        binding.sortByNameArrow.visibility = View.VISIBLE
        binding.sortBySizeArrow.visibility = View.GONE
        val sortedMap: Map<String, CountryItem>

        val listData = viewModel.countriesData.value
        if (listData != null){
            if (sortByNameState == Constants.ascending){
                sortedMap = listData.sortedByDescending { it.name }.map { it.alpha3Code!! to it }.toMap()
                sortByNameState = Constants.descending
                binding.sortByNameArrow.setImageResource(R.drawable.arrow_down)

            }
            else {
                sortedMap = listData.sortedBy { it.name }.map { it.alpha3Code!! to it }.toMap()
                sortByNameState = Constants.ascending
                binding.sortByNameArrow.setImageResource(R.drawable.arrow_up)

            }
            countriesMap = HashMap(sortedMap)
            initRecyclerView(sortedMap.values.toList())
        }
    }

    private fun sortCountryListBySize(){
        binding.sortByNameArrow.visibility = View.GONE
        binding.sortBySizeArrow.visibility = View.VISIBLE
        val sortedMap: Map<String, CountryItem>

        val listData = viewModel.countriesData.value
        if (listData != null) {
            if (sortBySizeState == Constants.ascending){
                sortedMap = listData.sortedByDescending { it.area }.map { it.alpha3Code!! to it }.toMap()
                sortBySizeState = Constants.descending
                binding.sortBySizeArrow.setImageResource(R.drawable.arrow_down)
            }
            else {
                sortedMap = listData.sortedBy { it.area }.map { it.alpha3Code!! to it }.toMap()
                sortBySizeState = Constants.ascending
                binding.sortBySizeArrow.setImageResource(R.drawable.arrow_up)
            }
            countriesMap=  HashMap(sortedMap)

            initRecyclerView(sortedMap.values.toList())

        }
    }

    private fun showProgressBar(){ binding.progressBar.visibility = View.VISIBLE }

    private fun hideProgressBar(){ binding.progressBar.visibility = View.GONE }


}