package com.taitsmith.weatherman.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * holds our response object that comes back from the openweathermap api
 */
data class WeatherResponseData (
    @SerializedName("coord")
    var coord: Coord? = Coord(),

    @SerializedName("weather")
    var weather: ArrayList<Weather> = arrayListOf(),

    @SerializedName("base")
    var base: String? = null,

    @SerializedName("main")
    var main: Main? = Main(),

    @SerializedName("visibility")
    var visibility: Int? = null,

    @SerializedName("wind")
    var wind: Wind? = Wind(),

    @SerializedName("clouds")
    var clouds: Clouds? = Clouds(),

    @SerializedName("dt")
    var dt: Int? = null,

    @SerializedName("sys")
    var sys: Sys? = Sys(),

    @SerializedName("timezone")
    var timezone: Int? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("cod")
    var cod: Int?= null
) : Serializable

data class Coord (
    @SerializedName("lon")
    var lon: Double? = null,

    @SerializedName("lat")
    var lat: Double? = null
)

data class Weather (
    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("main")
    var main: String? = null,

    @SerializedName("description")
    var description: String? = null,

    @SerializedName("icon")
    var icon: String? = null
)

data class Main (
    @SerializedName("temp")
    var temp: Double? = null,

    @SerializedName("feels_like")
    var feelsLike: Double? = null,

    @SerializedName("temp_min")
    var tempMin: Double? = null,

    @SerializedName("temp_max")
    var tempMax: Double? = null,

    @SerializedName("pressure")
    var pressure: Int? = null,

    @SerializedName("humidity")
    var humidity: Int? = null,
) {

    //turn temps to an int because whole numbers look better than saying it's 89.23 degrees
    fun getTempInt() : Int  { return temp!!.toInt() }
    fun getMaxInt() : Int  { return tempMax!!.toInt()}
    fun getMinInt() : Int { return tempMin!!.toInt()}
}

data class Wind (
    @SerializedName("speed")
    var speed: Double? = null,

    @SerializedName("deg")
    var deg: Int? = null,

    @SerializedName("gust")
    var gust: Double? = null
) {
    fun getWindInt(): Int {
        return speed!!.toInt()
    }
}


data class Clouds (
    @SerializedName("all")
    var all : Int? = null
)

data class Sys (
    @SerializedName("type")
    var type: Int? = null,

    @SerializedName("id")
    var id: Int? = null,

    @SerializedName("country")
    var country: String? = null,

    @SerializedName("sunrise")
    var sunrise: Int?    = null,

    @SerializedName("sunset")
    var sunset : Int?    = null
)