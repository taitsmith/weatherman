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

    private val _statusMessage = MutableLiveData<String>()
    val statusMessage: LiveData<String> = _statusMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    var lastLocation: Location? = null

    //get weather in background for given lat/lon. can be called by selecting a location from
    //search results list or by using the device's physical location. the api occasionally times out
    //so we use runcatching to handle that (or we could just increase the timeout on the call(
    fun getWeather(lat: Double, lon: Double) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                _weatherResponse.postValue(Event(apiRepository.getWeatherForLocation(lat, lon)))
            }.onFailure {
                _statusMessage.postValue("NETWORK_FAILURE")
            }
            _isLoading.postValue(false)
        }
    }

    //assuming the user has entered a non-empty search, we have to get the lat/lon coords to be
    //able to find the weather. some searches return multiple results so we'll display those and
    //allow users to pick which one they want. in the case of empty results (for a nonsense search
    //like 'alsdkfha' we'll display an error message
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getGeoData(city: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val geo = Event(apiRepository.getGeoDataFromCity(city))
            if (geo.peekContent().isEmpty()) {
                _statusMessage.postValue("NO_RESULTS")
                _geoResponse.postValue(geo)
                return@launch
            } else {
                _geoResponse.postValue(geo)
            }
            _isLoading.postValue(false)
        }
    }

    //reads lat and lon strings from shared prefs, checks they're valid and returns a latlng if true
    fun getLastSearch() : LatLng? {
        val sharedPrefs = application.getSharedPreferences("LAST_CITY", Context.MODE_PRIVATE)
        val lat = sharedPrefs.getString("LAT", "0.0")?.toDouble()
        val lon = sharedPrefs.getString("LON", "0.0")?.toDouble()
        return if (lat == 0.0 || lon == 0.0) null
        else LatLng(lat!!, lon!!)
    }

    //when the user picks a location from the list of results, store the latlng data to be read later
    //could also convert the latlng to a string, store that and then separate it out with .split()
    fun saveLastSearch(last: LatLng) {
        val sharedPrefs = application.getSharedPreferences("LAST_CITY", Context.MODE_PRIVATE)
        with (sharedPrefs.edit()) {
            putString("LAT", last.latitude.toString())
            putString("LON", last.longitude.toString())
            apply()
        }
    }

    //don't allow empty searches
    fun validateInput(cityInput: String) {
        if (cityInput.isEmpty()) _statusMessage.value = "BAD_INPUT"
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

    fun setStatusMessage(status: String) {
        _statusMessage.value = status
    }
}