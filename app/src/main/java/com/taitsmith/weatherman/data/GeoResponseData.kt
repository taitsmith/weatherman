package com.taitsmith.weatherman.data

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.gson.annotations.SerializedName
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.taitsmith.weatherman.R
import com.taitsmith.weatherman.databinding.GeodataListItemBinding
import java.io.Serializable

/**
 * holds response data from calling the openweathermap geocoder api. extends abstractbindingitem
 * so it can be used with fastadapter for recycler views
 */
class GeoResponseData : AbstractBindingItem<GeodataListItemBinding>(), Serializable{
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

    @SuppressLint("SetTextI18n")
    override fun bindView(binding: GeodataListItemBinding, payloads: List<Any>) {
        binding.geoListItemName.text = ("$name, $state, $country")
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