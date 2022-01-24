package com.bumie.nearme_

import android.os.Parcel
import android.os.Parcelable

data class Datum(
    var name:String,
    var category:String,
    var type:String,
    var subType:String,
  //  var geoCode: GeoCode,
    var rank:Int,
    var tags:List<String>): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
       // parcel.readString()!!,
        parcel.readInt(),
        parcel.createStringArrayList()!!
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeString(subType)
        parcel.writeString(category)
       // parcel.writeValue(geoCode)
        parcel.writeInt(rank)
        parcel.writeStringList(tags)
    }

    companion object CREATOR : Parcelable.Creator<Datum> {
        override fun createFromParcel(parcel: Parcel): Datum {
            return Datum(parcel)
        }

        override fun newArray(size: Int): Array<Datum?> {
            return arrayOfNulls(size)
        }
    }


}
