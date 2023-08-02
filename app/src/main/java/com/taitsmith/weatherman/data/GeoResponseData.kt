package com.taitsmith.weatherman.data

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.gson.annotations.SerializedName
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.taitsmith.weatherman.R
import com.taitsmith.weatherman.databinding.GeodataListItemBinding

class GeoResponseData : AbstractBindingItem<GeodataListItemBinding>() {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("local_names")
    var localNames: LocalNames = LocalNames()

    @SerializedName("lat")
    var lat: Double = 0.0

    @SerializedName("lon")
    var lon: Double = 0.0

    @SerializedName("country")
    var country: String? = null

    @SerializedName("state")
    var state: String? = null

    override val type: Int
        get() = R.id.geodata_list_item

    override fun bindView(binding: GeodataListItemBinding, payloads: List<Any>) {
        binding.geoListItemName.text = name ?: "oops"
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): GeodataListItemBinding {
        return GeodataListItemBinding.inflate(inflater, parent, false)
    }

}

data class LocalNames(
    var names: List<String> = listOf()
)