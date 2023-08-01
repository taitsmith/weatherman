package com.taitsmith.weatherman.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taitsmith.weatherman.api.ApiRepository
import com.taitsmith.weatherman.data.WeatherResponseData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val apiRepository: ApiRepository
): ViewModel() {

    private val _weatherResponse = MutableLiveData<WeatherResponseData>()
    val weatherResponse: LiveData<WeatherResponseData> = _weatherResponse

    fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherResponse.postValue(apiRepository.getWeatherForLocation(lat, lon))
        }
    }

    fun getGeoData(city: String, state: String) {
        viewModelScope.launch {
            apiRepository.getGeoDataFromCity(city, state)
        }
    }
}