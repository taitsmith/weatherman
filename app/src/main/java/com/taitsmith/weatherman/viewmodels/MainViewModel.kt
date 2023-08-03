package com.taitsmith.weatherman.viewmodels

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.annotation.VisibleForTesting
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.taitsmith.weatherman.api.ApiRepository
import com.taitsmith.weatherman.data.Event
import com.taitsmith.weatherman.data.GeoResponseData
import com.taitsmith.weatherman.data.WeatherResponseData
import com.taitsmith.weatherman.di.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val locationRepository: LocationRepository,
    private val application: Application
): AndroidViewModel(application) {

    private val _weatherResponse = MutableLiveData<Event<WeatherResponseData>>()
    val weatherResponse: LiveData<Event<WeatherResponseData>> = _weatherResponse

    private val _geoResponse = MutableLiveData<Event<List<GeoResponseData>>>()
    val geoResponse: LiveData<Event<List<GeoResponseData>>> = _geoResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    var lastLocation: Location? = null

    fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                _weatherResponse.postValue(Event(apiRepository.getWeatherForLocation(lat, lon)))
            }.onFailure {
                _errorMessage.postValue("NETWORK_FAILURE")
            }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getGeoData(city: String) {
        viewModelScope.launch {
            val geo = Event(apiRepository.getGeoDataFromCity(city))
            if (geo.peekContent().isEmpty()) {
                _errorMessage.postValue("NO_RESULTS")
                return@launch
            } else {
                _geoResponse.postValue(geo)
            }
        }
    }

    fun getLastSearch() : LatLng? {
        val sharedPrefs = application.getSharedPreferences("LAST_CITY", Context.MODE_PRIVATE)
        val lat = sharedPrefs.getString("LAT", "0.0")?.toDouble()
        val lon = sharedPrefs.getString("LON", "0.0")?.toDouble()
        return if (lat == 0.0 || lon == 0.0) null
        else LatLng(lat!!, lon!!)
    }

    fun saveLastSearch(last: LatLng) {
        val sharedPrefs = application.getSharedPreferences("LAST_CITY", Context.MODE_PRIVATE)
        with (sharedPrefs.edit()) {
            putString("LAT", last.latitude.toString())
            putString("LON", last.longitude.toString())
            apply()
        }
    }

    fun validateInput(cityInput: String) {
        if (cityInput.isEmpty()) _errorMessage.value = "BAD_INPUT"
        else getGeoData(cityInput)
    }

    fun startLocationUpdates() {
        locationRepository.startLocationUpdates()
        viewModelScope.launch {
            locationRepository.lastLocation.collect {
                if (it == null) return@collect
                if (it.longitude != 0.0) { //wait until we have an actual location
                    getWeather(it.latitude, it.longitude)
                    lastLocation = it
                }
            }
        }
    }

    fun stopLocationUpdates() {
        locationRepository.stopLocationUpdates()
    }

    fun locationEnabled() : Boolean {
        val lm = application.baseContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(lm)
    }

    fun setErrorMessage(error: String) {
        _errorMessage.value = error
    }
}