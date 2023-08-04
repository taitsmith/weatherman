package com.taitsmith.weatherman.ui

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.taitsmith.weatherman.R
import com.taitsmith.weatherman.databinding.ActivityMainBinding
import com.taitsmith.weatherman.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var navFragment: NavHostFragment
    private lateinit var binding: ActivityMainBinding

    private var lastSearch: LatLng? = null

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navFragment.navController

        lastSearch = mainViewModel.getLastSearch()

        binding.buttonSearch.setOnClickListener {
            mainViewModel.validateInput(binding.cityInput.text.toString())
            closeKeyboard()
        }

        getLocation()
        setObservers()

        binding.fab.setOnClickListener {
            if (mainViewModel.lastLocation != null) {
                mainViewModel.getWeather(
                    mainViewModel.lastLocation!!.latitude,
                    mainViewModel.lastLocation!!.longitude
                )
            }
        }
    }

    private fun closeKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun setObservers() {
        mainViewModel.statusMessage.observe(this) {
           val s = when(it) {
               "NETWORK_FAILURE"    -> getString(R.string.status_network_error)
               "BAD_INPUT"          -> getString(R.string.status_input_error)
               "NO_RESULTS"         -> getString(R.string.status_empty_result_error)
               "FETCHING_LAST"      -> getString(R.string.status_fetching_last)
               "FETCHING_CURRENT"   -> getString(R.string.status_fetching_current)
               else                 -> getString(R.string.status_generic_error)
           }
            showSnackbar(s)
        }

        mainViewModel.isLoading.observe(this) {
            when (it) {
                true    -> binding.mainLoadingIcon.visibility = View.VISIBLE
                false   -> binding.mainLoadingIcon.visibility = View.INVISIBLE
            }
        }
    }

    //ask for permission to access location, if granted start listening for updates and then find
    //weather for that location. otherwise get and display weather for the last selected location
    private fun getLocation() {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    if (mainViewModel.locationEnabled()) {
                        //we only want to bother with this if the location setting is enabled
                        mainViewModel.setStatusMessage("FETCHING_CURRENT")
                        mainViewModel.startLocationUpdates()
                    } else getLast()
                }
                else -> getLast()
            }
        }
        locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    private fun getLast() {
        if (lastSearch != null) {
            mainViewModel.setStatusMessage("FETCHING_LAST")
            mainViewModel.getWeather(lastSearch!!.latitude, lastSearch!!.longitude)
        }
    }

    private fun showSnackbar(message: String) {
       Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.stopLocationUpdates()
    }
}