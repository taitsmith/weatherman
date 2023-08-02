package com.taitsmith.weatherman.util

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.taitsmith.weatherman.R
import com.taitsmith.weatherman.data.GeoResponseData
import com.taitsmith.weatherman.databinding.GeodataListItemBinding

class GeoListAdapter : AbstractBindingItem<GeodataListItemBinding>() {
    var city: GeoResponseData? = null

    override val type: Int
        get() = R.id.geodata_list_item

    override fun bindView(binding: GeodataListItemBinding, payloads: List<Any>) {
        binding.geoListItemName.text = city?.name ?: "oops"
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): GeodataListItemBinding {
        return GeodataListItemBinding.inflate(inflater, parent, false)
    }


}