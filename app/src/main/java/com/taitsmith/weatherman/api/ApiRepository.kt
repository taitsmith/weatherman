package com.taitsmith.weatherman.api

import com.taitsmith.weatherman.data.GeoResponseData
import com.taitsmith.weatherman.data.WeatherResponseData
import com.taitsmith.weatherman.di.GeocoderApiInterface
import com.taitsmith.weatherman.di.WeatherApiInterface
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Inject


@Module
@InstallIn(ViewModelComponent::class)
class ApiRepository @Inject constructor(
    @WeatherApiInterface
    val weatherApiInterface:    ApiInterface,
    @GeocoderApiInterface
    val geocoderApiInterface:   ApiInterface
) {
    suspend fun getWeatherForLocation(
        lat: Double,
        lon: Double,
        units: String = "imperial"
    ) : WeatherResponseData {
        return weatherApiInterface.getLocalWeather(lat, lon, units)
    }

    suspend fun getGeoDataFromCity(
        city: String,
        state: String?,
    ) : List<GeoResponseData> {

        //obviously comes back as "oakland,,us" if state is left blank, but this functions properly
        //went sent to the api
        val formattedCityState = "$city,$state,us"
        return geocoderApiInterface.getGeocodedLocation(formattedCityState)
    }
}