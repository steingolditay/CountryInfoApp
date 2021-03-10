package com.steingolditay.app.matrixapp.ui.adapters

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.steingolditay.app.matrixapp.R
import com.steingolditay.app.matrixapp.model.CountryItem

class CountryListAdapter(private val context: Activity,
                         private val countriesList: List<CountryItem>?,
                         private val listener: OnItemClickListener)
    : RecyclerView.Adapter<CountryListAdapter.ViewHolder>() {


    // set an on click listener interface for rows
    interface OnItemClickListener{
        fun onItemClick(countryItem: CountryItem)
    }

    inner class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.countryName)
        val nativeName: TextView = itemView.findViewById(R.id.countryNativeName)
        val areaSize: TextView = itemView.findViewById(R.id.countryAreaSize)
        val flag: ImageView = itemView.findViewById(R.id.flag)

        init {
            // init marquee effect
            name.isSelected = true
            nativeName.isSelected = true
            areaSize.isSelected = true

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && countriesList != null){
                    listener.onItemClick(countriesList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_country_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val country = countriesList!![position]
        holder.name.text = country.name
        holder.nativeName.text = country.nativeName
        holder.areaSize.text = country.area.toBigDecimal().toPlainString()
        GlideToVectorYou.justLoadImage(context, Uri.parse(country.flag), holder.flag)
    }

    override fun getItemCount(): Int {
        return countriesList?.size ?: 0
    }

}