package ru.vlasov.vkcupapp.features.taxi

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.vlasov.vkcupapp.R
import ru.vlasov.vkcupapp.models.map.LocationData
import ru.vlasov.vkcupapp.utils.LocationDataConverter


class LocationsListAdapter(private val listener : LocationPickListener) :
        ListAdapter<LocationData, LocationsListAdapter.ViewHolder>(LocationsDiffUtil()) {


    interface LocationPickListener{
        fun onLocationClick(locationData: LocationData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.location_item, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(locationData = getItem(position), listener)
    }



    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var locationName : TextView? = null
        var locationDetail : TextView? = null
        init{
            locationName = itemView.findViewById(R.id.locationName)
            locationDetail = itemView.findViewById(R.id.locationDetail)
        }

        fun bind(locationData: LocationData, listener : LocationPickListener) {
            locationName?.text = LocationDataConverter.convertLocationDataToString(locationData)
            itemView.setOnClickListener{
                listener.onLocationClick(locationData)
            }
        }

    }

    class LocationsDiffUtil : DiffUtil.ItemCallback<LocationData>(){
        override fun areItemsTheSame(oldItem: LocationData, newItem: LocationData): Boolean =
            oldItem.lat == newItem.lat && oldItem.lon == newItem.lon

        override fun areContentsTheSame(oldItem: LocationData, newItem: LocationData): Boolean =
            oldItem == newItem

    }
}