package com.taitsmith.weatherman.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.taitsmith.weatherman.data.GeoResponseData
import com.taitsmith.weatherman.databinding.FragmentMainBinding
import com.taitsmith.weatherman.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentMain : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val geoAdapter = ItemAdapter<GeoResponseData>()
    private val fastAdapter = FastAdapter.with(geoAdapter)

    private var _binding: FragmentMainBinding? = null
    private var _geoListView: RecyclerView? = null

    private val binding get() = _binding!!
    private val geoListView get() = _geoListView!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        _geoListView = binding.geoListRecycler

        geoListView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        mainViewModel.getGeoData(mainViewModel.getLastSearch())

        setObservers()

        return binding.root
    }

    private fun setObservers() {
        fastAdapter.onClickListener = { _, _, item, _ ->
            mainViewModel.getWeather(item.lat, item.lon)
            false
        }

        mainViewModel.geoResponse.observe(
              viewLifecycleOwner
        ) {
            geoListView.adapter = fastAdapter
            geoAdapter.add(it)
        }

        mainViewModel.weatherResponse.observe(
            viewLifecycleOwner
        ) {
            val action = FragmentMainDirections.actionFirstFragmentToSecondFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSearch.setOnClickListener {
            geoAdapter.clear()
            mainViewModel.validateInput(binding.cityInput.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}