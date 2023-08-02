package com.taitsmith.weatherman.api

import com.taitsmith.weatherman.data.GeoResponseData
import com.taitsmith.weatherman.data.WeatherResponseData
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * holds all of our retrofit calls. hard coding in the api key is obviously bad practice, and i'd
 * normally hide it somewhere like gradle.properties and access it as a BuildConfig.api_key or
 * something similar. for the sake of simplicity we'll leave it here though.
 */
interface ApiInterface {

    @GET("weather/")
    suspend fun getLocalWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String,
        @Query("appid") appid: String = "8f37ce56969484b82dce75161bc99891"
    ) : WeatherResponseData


    @GET("direct?")
    suspend fun getGeocodedLocation(
        @Query("q", encoded = true) cityName: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") appid: String = "8f37ce56969484b82dce75161bc99891"
    ) : List<GeoResponseData>
}