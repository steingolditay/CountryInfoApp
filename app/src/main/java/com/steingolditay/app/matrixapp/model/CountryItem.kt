package com.steingolditay.app.matrixapp.model

import android.os.Parcel
import android.os.Parcelable

data class CountryItem(
    var name: String?,
    var nativeName: String?,
    var alpha3Code: String?,
    var area: Float,
    var borders: List<String>?,
    var flag: String?,

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readFloat(),
        parcel.createStringArrayList(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(nativeName)
        parcel.writeString(alpha3Code)
        parcel.writeFloat(area)
        parcel.writeStringList(borders)
        parcel.writeString(flag)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CountryItem> {
        override fun createFromParcel(parcel: Parcel): CountryItem {
            return CountryItem(parcel)
        }

        override fun newArray(size: Int): Array<CountryItem?> {
            return arrayOfNulls(size)
        }
    }
}
