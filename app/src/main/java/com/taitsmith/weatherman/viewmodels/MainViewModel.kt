package com.taitsmith.weatherman.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taitsmith.weatherman.api.ApiRepository
import com.taitsmith.weatherman.data.GeoResponseData
import com.taitsmith.weatherman.data.WeatherResponseData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiRepository: ApiRepository,
    private val application: Application
): AndroidViewModel(application) {

    private val _weatherResponse = MutableLiveData<WeatherResponseData>()
    val weatherResponse: LiveData<WeatherResponseData> = _weatherResponse

    private val _geoResponse = MutableLiveData<List<GeoResponseData>>()
    val geoResponse: LiveData<List<GeoResponseData>> = _geoResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherResponse.postValue(apiRepository.getWeatherForLocation(lat, lon))
        }
    }

    fun getGeoData(city: String) {
        viewModelScope.launch {
            _geoResponse.postValue(apiRepository.getGeoDataFromCity(city))
        }
    }

    fun getLastSearch() : String {
        val sharedPrefs = application.getSharedPreferences("LAST_CITY", Context.MODE_PRIVATE)
        return sharedPrefs.getString("LAST_CITY", "oakland").toString()
    }

    private fun saveLastSearch(last: String) {
        val sharedPrefs = application.getSharedPreferences("LAST_CITY", Context.MODE_PRIVATE)
        with (sharedPrefs.edit()) {
            putString("LAST_CITY", last)
            apply()
        }
    }

    fun validateInput(cityInput: String) {
        if (cityInput.isEmpty()) _errorMessage.value = "BAD_INPUT"
        else {
            getGeoData(cityInput)
            saveLastSearch(cityInput)
        }
    }
}