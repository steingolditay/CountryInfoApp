package com.steingolditay.app.matrixapp.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
    private lateinit var countryItem: CountryItem
    private lateinit var borderCountriesList: ArrayList<CountryItem>

    private var sortByNameState = Constants.descending
    private var sortBySizeState = Constants.descending

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountryBordersListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bundle = intent.extras
        if (bundle != null){
            countryItem = bundle.getParcelable(Constants.countryItem)!!
            borderCountriesList = bundle.getParcelableArrayList(Constants.borderCountriesItems)!!
            updateUIWithCountryData()

        }

        binding.sortByName.setOnClickListener{
            sortCountryListByName()
        }

        binding.sortBySize.setOnClickListener {
            sortCountryListBySize()
        }

    }


    private fun updateUIWithCountryData(){
        GlideToVectorYou.justLoadImage(this, Uri.parse(countryItem.flag), binding.flag)
        binding.countryName.text = countryItem.name
        binding.countryNativeName.text = countryItem.nativeName

        if (borderCountriesList.isNotEmpty()){
            initRecyclerView(borderCountriesList.toList())
        }
        else {
            updateUINoBorderCountriesToShow()
        }

    }

    // load border countries to recycler view
    private fun initRecyclerView(borderCountryList: List<CountryItem>) {
        adapter = CountryBordersListAdapter(this, borderCountryList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private fun updateUINoBorderCountriesToShow(){
        binding.recyclerView.visibility = View.GONE
        binding.sortByName.visibility = View.GONE
        binding.sortByNameArrow.visibility = View.GONE
        binding.sortBySize.visibility = View.GONE
        binding.sortBySizeArrow.visibility = View.GONE

        binding.noCountries.visibility = View.VISIBLE
    }

    private fun sortCountryListByName(){
        binding.sortByNameArrow.visibility = View.VISIBLE
        binding.sortBySizeArrow.visibility = View.GONE
        val sortedMap: List<CountryItem>
            if (sortByNameState == Constants.ascending){
                sortedMap = borderCountriesList.sortedByDescending {it.name }
                sortByNameState = Constants.descending
                binding.sortByNameArrow.setImageResource(R.drawable.arrow_down)

            }
            else {
                sortedMap = borderCountriesList.sortedBy { it.name }
                sortByNameState = Constants.ascending
                binding.sortByNameArrow.setImageResource(R.drawable.arrow_up)

            }
            initRecyclerView(sortedMap)
    }

    private fun sortCountryListBySize(){
        binding.sortByNameArrow.visibility = View.GONE
        binding.sortBySizeArrow.visibility = View.VISIBLE

        val sortedMap: List<CountryItem>
        if (sortBySizeState == Constants.ascending){
            sortedMap = borderCountriesList.sortedByDescending {it.area }
            sortBySizeState = Constants.descending
            binding.sortBySizeArrow.setImageResource(R.drawable.arrow_down)

        }
        else {
            sortedMap = borderCountriesList.sortedBy { it.area }
            sortBySizeState = Constants.ascending
            binding.sortBySizeArrow.setImageResource(R.drawable.arrow_up)

        }
        initRecyclerView(sortedMap)

    }

}