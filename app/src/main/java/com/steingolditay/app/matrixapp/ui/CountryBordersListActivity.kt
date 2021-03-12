package com.steingolditay.app.matrixapp.ui

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.steingolditay.app.matrixapp.R
import com.steingolditay.app.matrixapp.databinding.ActivityCountryBordersListBinding
import com.steingolditay.app.matrixapp.model.CountryItem
import com.steingolditay.app.matrixapp.utils.Constants
import com.steingolditay.app.matrixapp.ui.adapters.CountryBordersListAdapter
import java.util.ArrayList


class CountryBordersListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCountryBordersListBinding
    private lateinit var adapter: CountryBordersListAdapter
    private lateinit var presentedCountryItem: CountryItem
    private lateinit var borderCountriesList: ArrayList<CountryItem>

    private lateinit var arrowUpDrawable: Drawable
    private lateinit var arrowDownDrawable: Drawable

    private var sortByNameState = Constants.descending
    private var sortBySizeState = Constants.descending

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryBordersListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        arrowUpDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.arrow_up)!!
        arrowDownDrawable = ContextCompat.getDrawable(applicationContext, R.drawable.arrow_down)!!

        getDataFromBundle()


        binding.sortByNameButton.setOnClickListener{
            sortCountryListByName()
        }

        binding.sortBySizeButton.setOnClickListener {
            sortCountryListBySize()
        }

    }

    // receive selected country data
    // and its border countries items
    private fun getDataFromBundle(){
        val bundle = intent.extras
        if (bundle != null){
            if (bundle.keySet().contains(Constants.countryItem) && bundle.keySet().contains(Constants.borderCountriesItems)){
                presentedCountryItem = bundle.getParcelable(Constants.countryItem)!!
                borderCountriesList = bundle.getParcelableArrayList(Constants.borderCountriesItems)!!
                updateUiData()
            }
        }
    }

    private fun updateUiData(){
        GlideToVectorYou.justLoadImage(this, Uri.parse(presentedCountryItem.flag), binding.flag)
        binding.countryName.text = presentedCountryItem.name
        binding.countryNativeName.text = presentedCountryItem.nativeName

        if (borderCountriesList.isNotEmpty()){
            initRecyclerView(borderCountriesList.toList())
        }
        else {
            updateUiNoBorderCountriesToShow()
        }

    }

    // load border countries to recycler view
    private fun initRecyclerView(borderCountryList: List<CountryItem>) {
        adapter = CountryBordersListAdapter(this, borderCountryList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    // shows if presented country has no border countries
    private fun updateUiNoBorderCountriesToShow(){
        binding.recyclerView.visibility = View.GONE
        binding.sortByNameButton.visibility = View.GONE
        binding.sortBySizeButton.visibility = View.GONE

        binding.noCountries.visibility = View.VISIBLE
    }

    private fun sortCountryListByName(){
        setSortBySizeDrawable(null)
        val sortedMap: List<CountryItem>
            if (sortByNameState == Constants.ascending){
                sortedMap = borderCountriesList.sortedByDescending {it.name }
                sortByNameState = Constants.descending
                setSortByNameDrawable(arrowDownDrawable)

            }
            else {
                sortedMap = borderCountriesList.sortedBy { it.name }
                sortByNameState = Constants.ascending
                setSortByNameDrawable(arrowUpDrawable)

            }
            initRecyclerView(sortedMap)
    }

    private fun sortCountryListBySize(){
        setSortByNameDrawable(null)

        val sortedMap: List<CountryItem>
        if (sortBySizeState == Constants.ascending){
            sortedMap = borderCountriesList.sortedByDescending {it.area }
            sortBySizeState = Constants.descending
            setSortBySizeDrawable(arrowDownDrawable)

        }
        else {
            sortedMap = borderCountriesList.sortedBy { it.area }
            sortBySizeState = Constants.ascending
            setSortBySizeDrawable(arrowUpDrawable)

        }
        initRecyclerView(sortedMap)

    }

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