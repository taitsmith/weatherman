package com.taitsmith.weatherman.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.taitsmith.weatherman.R
import com.taitsmith.weatherman.databinding.FragmentWeatherDetailBinding
import com.taitsmith.weatherman.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentWeatherDetail : Fragment() {
    private val args: FragmentWeatherDetailArgs by navArgs()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val weather by lazy {
        args.weather
    }
    private lateinit var url: String

    private var _binding: FragmentWeatherDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherDetailBinding.inflate(inflater, container, false)
        binding.weather = weather

        url = getString(R.string.weather_detail_image_url, weather!!.weather[0].icon)
        binding.weatherDetailImageview.load(url) {
            crossfade(true)
            placeholder(R.drawable.baseline_cached_24)
        }

        setObserver()

        return binding.root
    }

    private fun setObserver() {
        mainViewModel.geoResponse.observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { geo ->
                val action = FragmentWeatherDetailDirections.actionSecondFragmentToFirstFragment(geo[0])
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}