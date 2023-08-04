package com.taitsmith.weatherman.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.taitsmith.weatherman.data.GeoResponseData
import com.taitsmith.weatherman.databinding.FragmentMainBinding
import com.taitsmith.weatherman.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentMain : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val args: FragmentMainArgs by navArgs()

    private val geo by lazy {
        args.geoResponse
    }

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

        if (geo != null) {
            geoAdapter.clear()
            geoListView.adapter = fastAdapter
            geoAdapter.add(geo!!)
        }

        geoListView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        setObservers()

        return binding.root
    }

    private fun setObservers() {
        fastAdapter.onClickListener = { _, _, item, _ ->
            mainViewModel.getWeather(item.lat, item.lon)
            mainViewModel.saveLastSearch(LatLng(item.lat, item.lon))
            false
        }

        mainViewModel.geoResponse.observe(
            viewLifecycleOwner
        ) {
            it.getContentIfNotHandled()?.let { geoData ->
                geoAdapter.clear()
                geoListView.adapter = fastAdapter
                geoAdapter.add(geoData)
            }
        }

        mainViewModel.weatherResponse.observe(
            viewLifecycleOwner
        ) {
            it.getContentIfNotHandled()?.let { wr ->
                val action = FragmentMainDirections.actionFirstFragmentToSecondFragment(wr)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}