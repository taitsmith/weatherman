package com.taitsmith.weatherman

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.taitsmith.weatherman.api.ApiRepository
import com.taitsmith.weatherman.data.Coord
import com.taitsmith.weatherman.data.GeoResponseData
import com.taitsmith.weatherman.data.Main
import com.taitsmith.weatherman.data.Weather
import com.taitsmith.weatherman.data.WeatherResponseData
import com.taitsmith.weatherman.di.LocationRepository
import com.taitsmith.weatherman.viewmodels.MainViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doThrow
import retrofit2.HttpException

@RunWith(JUnit4::class)
class MainViewModelTest {
    @get:Rule
    var rule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatchRule()

    @Mock
    private lateinit var locationRepository: LocationRepository
    @Mock
    private lateinit var apiRepository: ApiRepository
    @Mock
    private lateinit var application: Application

    private lateinit var mainViewModel: MainViewModel

    private var mockedWeatherResponse = WeatherResponseData()
    private var mockedGeoResponse = GeoResponseData()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        createMockedWeather()
        createMockedGeoData()

        mainViewModel = MainViewModel(apiRepository, locationRepository, application)
    }

    @After
    fun teardown() {
        testDispatcher.cancel()
    }

    private fun createMockedGeoData() {
        mockedGeoResponse = GeoResponseData()
        mockedGeoResponse.name      = "Cool Town"
        mockedGeoResponse.lat       = 32.23
        mockedGeoResponse.lon       = -122.42
        mockedGeoResponse.state     = "California"
        mockedGeoResponse.country   = "US"
    }

    private fun createMockedWeather() {
        mockedWeatherResponse = WeatherResponseData(
            coord = Coord(
                lat = 32.0,
                lon = 122.03
            ),
            weather = arrayListOf(Weather(
                main = "Clouds",
                description = "Wow it's very cloudy today"
            )),
            main = Main(
                temp = 78.8,
                tempMin = 45.5,
                tempMax = 89.3
            ),
            name = "Cool Town",
        )
    }

    @Test
    fun `test geodata api updates live data`() = runTest {
        `when`(apiRepository.getGeoDataFromCity("Cool Town")).thenReturn(listOf(mockedGeoResponse))
        mainViewModel.getGeoData("Cool Town")

        val returnedGeoData = apiRepository.getGeoDataFromCity("Cool Town")
        val vmData = mainViewModel.geoResponse.getOrAwaitValue().peekContent()
        assertEquals(returnedGeoData, vmData)
    }

    @Test
    fun `test weather api updates live data`() = runTest {
        `when`(apiRepository.getWeatherForLocation(32.0, 122.03)).thenReturn(mockedWeatherResponse)
        mainViewModel.getWeather(32.0, 122.03)

        val returnedWeather = apiRepository.getWeatherForLocation(32.0, 122.03)
        val weatherData = mainViewModel.weatherResponse.getOrAwaitValue().peekContent()
        assertEquals(returnedWeather, weatherData)
    }

    @Test
    fun `test error message updates live data`() = runTest {
        mainViewModel.setStatusMessage("GENERIC_ERROR")
        assertEquals("GENERIC_ERROR", mainViewModel.statusMessage.getOrAwaitValue())
    }

    @Test
    fun `test empty geo data sets error message`() = runTest {
        `when`(apiRepository.getGeoDataFromCity("empty")).thenReturn(listOf())
        mainViewModel.getGeoData("empty")

        assertEquals("NO_RESULTS", mainViewModel.statusMessage.getOrAwaitValue())
    }

    @Test
    fun `test network failure sets error message`() = runTest {
        doThrow(HttpException::class).`when`(apiRepository).getWeatherForLocation(0.0, 0.0)
        mainViewModel.getWeather(0.0, 0.0)
        assertEquals("NETWORK_FAILURE", mainViewModel.statusMessage.getOrAwaitValue())
    }

    @Test
    fun `test validate input validates input`() = runTest {
        `when`(apiRepository.getGeoDataFromCity("Cool Town")).thenReturn(listOf(mockedGeoResponse))
        mainViewModel.validateInput("")
        assertEquals("BAD_INPUT", mainViewModel.statusMessage.getOrAwaitValue())

        mainViewModel.validateInput("Cool Town")
        val vmData = mainViewModel.geoResponse.getOrAwaitValue().peekContent()
        assertEquals(mockedGeoResponse, vmData[0])
    }

}