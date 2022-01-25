package com.bumie.nearme_

class Places {
    var longitude: Double? = null
    var latitude: Double? = null
    var name: String? = null
    var category: String? = null

    constructor(longitude: Double?, latitude: Double?, name: String?, category: String?) {
        this.longitude = longitude
        this.latitude = latitude
        this.name = name
        this.category = category
    }

    constructor(longitude: Double?, latitude: Double?) {
        this.longitude = longitude
        this.latitude = latitude
    }


}