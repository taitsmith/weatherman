package com.taitsmith.weatherman.data

import com.google.gson.annotations.SerializedName

data class GeoResponseData(
    @SerializedName("name")
    var name: String? = null,

    @SerializedName("local_names")
    var localNames: LocalNames = LocalNames(),

    @SerializedName("lat")
    var lat: Double = 0.0,

    @SerializedName("lon")
    var lon: Double = 0.0,

    @SerializedName("country")
    var country: String? = null,

    @SerializedName("state")
    var state: String? = null
)

data class LocalNames(
    var names: List<String> = listOf()
)