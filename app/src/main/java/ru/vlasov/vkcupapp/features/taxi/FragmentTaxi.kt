package ru.vlasov.vkcupapp.features.taxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.vlasov.vkcupapp.R
import ru.vlasov.vkcupapp.databinding.FragmentTaxiBinding
import ru.vlasov.vkcupapp.models.map.LocationData

class FragmentTaxi  : Fragment()  {

    companion object{
        const val START_LOCATION_KEY = "TAXI_START_LOCATION"
        const val DESTINATION_KEY = "TAXI_DESTINATION"
    }

    var binding : FragmentTaxiBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaxiBinding.inflate(inflater)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
                .replace(R.id.mapContentPlaceholder, FragmentMap.newInstance(object : FragmentMap.EditTextClickListener{
                    override fun onEditTextClickListener(
                        startLocationData: LocationData?,
                        destinationData: LocationData?
                    ) {
                        navigateToFindLocation(startLocationData, destinationData)
                    }
                }))
                .commit()
    }


    private fun navigateToFindLocation( startLocationData: LocationData?,
                                        destinationData: LocationData?){
        childFragmentManager.beginTransaction()
            .replace(R.id.mapContentPlaceholder, FragmentFindLocation.newInstance(startLocationData, destinationData,
            object : FragmentFindLocation.LocationClickListener{
                override fun onLocationClick(
                    startLocation: LocationData?,
                    destinationLocation: LocationData?)
                {
                    onLocationDefined(startLocation, destinationLocation)
                }
            },
            object : FragmentFindLocation.NavigationProviderForLocationList{
                override fun navigate(startLocationData: LocationData) {
                    navigateToMap(startLocationData)
                }
            }))
            .commit()
    }

    private fun navigateToMap(startLocationData: LocationData) {
        childFragmentManager.beginTransaction()
            .replace(R.id.mapContentPlaceholder, FragmentMap.newInstance(object : FragmentMap.EditTextClickListener{
                override fun onEditTextClickListener(
                    startLocationData: LocationData?,
                    destinationData: LocationData?
                ) {
                    navigateToFindLocation(startLocationData, destinationData)
                }
            }, startLocationData, null))
            .commit()
    }

    private fun onLocationDefined(startLocationData: LocationData?, destinationData: LocationData?){
        childFragmentManager.beginTransaction()
            .replace(R.id.mapContentPlaceholder, FragmentMap.newInstance(object : FragmentMap.EditTextClickListener{
                override fun onEditTextClickListener(
                    startLocationData: LocationData?,
                    destinationData: LocationData?
                ) {
                    navigateToFindLocation(startLocationData, destinationData)
                }
            },startLocationData, destinationData))
            .commit()
    }

}