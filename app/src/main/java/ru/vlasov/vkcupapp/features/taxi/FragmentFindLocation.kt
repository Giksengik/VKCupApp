package ru.vlasov.vkcupapp.features.taxi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.jakewharton.rxbinding.widget.RxTextView
import dagger.hilt.android.AndroidEntryPoint
import ru.vlasov.vkcupapp.databinding.FragmentFindLocationBinding
import ru.vlasov.vkcupapp.features.NetworkUser
import ru.vlasov.vkcupapp.models.map.LocationData
import ru.vlasov.vkcupapp.utils.LocationDataConverter
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action1
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class FragmentFindLocation() : Fragment() {

    constructor(startLocation : LocationData?, destinationLocation: LocationData?, locationClickListener: LocationClickListener,
                navigationProviderForLocationList: NavigationProviderForLocationList) : this(){
        this.startLocation = startLocation
        this.destinationLocation = destinationLocation
        this.locationClickListener = locationClickListener
        this.navigationProviderForLocationList = navigationProviderForLocationList
    }

    companion object{
        fun newInstance(startLocation : LocationData?, destinationLocation: LocationData?, locationClickListener: LocationClickListener,
        navigationProviderForLocationList: NavigationProviderForLocationList): FragmentFindLocation =
            FragmentFindLocation(startLocation, destinationLocation, locationClickListener,
                navigationProviderForLocationList)
    }

    interface LocationClickListener{
        fun onLocationClick(startLocation : LocationData?, destinationLocation: LocationData? )
    }

    interface NavigationProviderForLocationList {
        fun navigate(startLocationData: LocationData)
    }


    private var binding : FragmentFindLocationBinding? = null
    private var listAdapter : LocationsListAdapter? = null

    private val viewModel : FindLocationViewModel by viewModels()

    private var startLocation : LocationData? = null
    private var destinationLocation : LocationData? = null

    private var locationClickListener : LocationClickListener? = null
    private var navigationProviderForLocationList : NavigationProviderForLocationList? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindLocationBinding.inflate(inflater)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareUI(startLocation, destinationLocation)

        listAdapter = LocationsListAdapter(object : LocationsListAdapter.LocationPickListener {
            override fun onLocationClick(locationData: LocationData) {
                handleOnLocationClick(locationData)
            }
        }
        )
        binding?.locationsList?.apply {
             layoutManager =  LinearLayoutManager(requireContext())
             adapter = listAdapter
        }

        binding?.taxiWhereToField?.let { textField ->
            RxTextView.textChanges(textField)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe {
                    viewModel.loadLocationsByQuery(it.toString())
                }
        }

        binding?.buttonCancel?.setOnClickListener{
            locationClickListener?.onLocationClick(startLocation,destinationLocation)
        }

        viewModel.viewState.observe(viewLifecycleOwner){
            handleViewState(it)
        }
    }

    private fun prepareUI(startLocation : LocationData? , destinationLocation: LocationData?){
        binding?.taxiFromWhereField?.isEnabled = false
        binding?.taxiWhereToField?.requestFocus()
        if(startLocation != null)
            binding?.taxiFromWhereField?.setText(LocationDataConverter.convertLocationDataToString(startLocation))
        if(destinationLocation != null)
            binding?.taxiWhereToField?.setText(LocationDataConverter.convertLocationDataToString(destinationLocation))

    }

    private fun handleViewState(viewState: FindLocationViewState?) {
        when (viewState) {
            is FindLocationViewState.Success.LocationsLoaded -> submitLocationsList(viewState.locations)
            is FindLocationViewState.Error.NetworkError -> showNetworkError()
        }
    }

    private fun showNetworkError() =
        activity?.let{
            (it as NetworkUser).showNetworkError()
        }

    private fun submitLocationsList(locations: List<LocationData>){
        listAdapter?.submitList(locations)
    }

    private fun handleOnLocationClick(locationData: LocationData){
        locationClickListener?.onLocationClick(startLocation, locationData)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        listAdapter = null
        navigationProviderForLocationList = null
        locationClickListener = null
    }
}