package com.bumie.nearme_

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class GeoCode {
    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null

    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null

    constructor(latitude: Double?, longitude: Double?) {
        this.latitude = latitude
        this.longitude = longitude
    }
}