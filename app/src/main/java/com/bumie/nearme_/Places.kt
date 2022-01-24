package com.bumie.nearme_

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.bumie.nearme_.GeoCode

class Places {
    var longitude: Double? = null
    var latitude: Double? = null
    var geoCode: List<GeoCode>? = null
    var name: String? = null
    var category: String? = null
    var rank: Int? = null

    var tags: List<String>? = null

    constructor(longitude: Double?, latitude: Double?, name: String?, category: String?) {
        this.longitude = longitude
        this.latitude = latitude
        this.name = name
        this.category = category
    }


}