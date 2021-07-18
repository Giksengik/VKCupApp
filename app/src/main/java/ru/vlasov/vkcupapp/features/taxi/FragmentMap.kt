package ru.vlasov.vkcupapp.features.taxi

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mapsforge.core.graphics.Color
import org.osmdroid.api.IMapController
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import ru.vlasov.vkcupapp.R
import ru.vlasov.vkcupapp.databinding.FragmentMapBinding
import ru.vlasov.vkcupapp.features.NetworkUser
import ru.vlasov.vkcupapp.models.map.LocationData
import ru.vlasov.vkcupapp.network.json.Coordinates
import ru.vlasov.vkcupapp.utils.LocationDataConverter
import ru.vlasov.vkcupapp.utils.TaxiDataHelper


@AndroidEntryPoint
class FragmentMap() : Fragment(){

    companion object{
        fun newInstance(editTextClickListener: EditTextClickListener): FragmentMap{
            return FragmentMap(editTextClickListener = editTextClickListener)
        }
        fun newInstance(editTextClickListener: EditTextClickListener,
                        startLocationData: LocationData?, destination: LocationData?): FragmentMap{
            return FragmentMap(editTextClickListener = editTextClickListener, startLocationData = startLocationData, destination = destination)
        }
    }

    private constructor(editTextClickListener: EditTextClickListener?) : this(){
        this.editTextClickListener = editTextClickListener
    }

    private constructor(editTextClickListener: EditTextClickListener?,
                        startLocationData: LocationData?, destination: LocationData?) : this(){
        this.editTextClickListener = editTextClickListener
        this.startLocation = startLocationData
        this.destinationLocation = destination
    }

    var editTextClickListener : EditTextClickListener? = null

    var binding : FragmentMapBinding? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var isRationaleShown = false
    private var isPermissionGranted = false
    private var isLastLocationDefined = false
    private var isRouteDefined = false

    private var startLocation : LocationData? = null
    private var destinationLocation : LocationData? = null
    private var destinationMarker : Marker? = null
    private var startMarker : Marker? = null
    private var roadOverlay : Polyline? = null
    private var mapController : IMapController? = null
    private var completeRoad : Road? = null

    private val viewModel : MapViewModel by viewModels()

    interface EditTextClickListener{
        fun onEditTextClickListener(startLocationData: LocationData?, destinationData: LocationData?)
    }



    @SuppressLint("MissingPermission")
    override fun onAttach(context: Context) {
        super.onAttach(context)
        requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
        ){ isGranted: Boolean ->
            if(isGranted){
                isPermissionGranted = true
                onLocationPermissionGranted()
            }
            else onLocationPermissionNotGranted()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            Configuration.getInstance().load(it, PreferenceManager.getDefaultSharedPreferences(it))
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    private fun onLocationPermissionGranted() {
        isLastLocationDefined = true
        prepareMap()
        viewModel.getLastLocation()
    }

    private fun onLocationPermissionNotGranted() {
        activity?.let{
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareUI()

        binding?.map?.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean = onWorkWithMap()
            override fun onZoom(event: ZoomEvent?): Boolean = onWorkWithMap()
        })

        binding?.include?.buttonTaxiAction?.setOnClickListener{
            onActionButtonClick()
        }

        binding?.include?.imageCancelOrder?.setOnClickListener{
            hideBottomTaxiPanel()
            binding?.include?.buttonTaxiAction?.text = getString(R.string.done)
            isRouteDefined = false
        }

        binding?.include?.taxiFromWhereField?.setOnClickListener{
                if(!isRouteDefined)
                    editTextClickListener?.onEditTextClickListener(startLocation, destinationLocation) }


        binding?.include?.taxiWhereToField?.setOnClickListener{
            if(!isRouteDefined)
                editTextClickListener?.onEditTextClickListener(startLocation, destinationLocation)
        }

        binding?.include?.droneTaxiLayout?.setOnClickListener{
            onSelectLayout(binding?.include?.droneTaxiLayout, binding?.include?.droneTaxiPrice)
        }

        binding?.include?.niceTaxiLayout?.setOnClickListener{
            onSelectLayout(binding?.include?.niceTaxiLayout, binding?.include?.niceTaxiPrice)
        }

        binding?.include?.fastTaxiLayout?.setOnClickListener{
            onSelectLayout(binding?.include?.fastTaxiLayout, binding?.include?.fastTaxiPrice)
        }



        viewModel.viewState.observe(viewLifecycleOwner){
            handleState(it)
        }
    }

    private fun prepareUI() {
        if(startLocation != null){
            isLastLocationDefined = true
            setCurrentLocationToMapAndUI(startLocation!!)
        }
        if(destinationLocation != null){
            setUiWithDestination(destinationLocation!!)
        }
        if(destinationLocation != null && startLocation != null){
            drawRoute(destinationLocation!!)
            isRouteDefined = true
            showBottomTaxiPanel()
            setMapCenter(Coordinates(lat = destinationLocation!!.lat, lon = destinationLocation!!.lon))
        }
    }

    private fun onActionButtonClick() {
        if(isRouteCompleted()){
            isRouteDefined = true
            showBottomTaxiPanel()
        }
    }

    private fun showBottomTaxiPanel() {
        binding?.locationLabel?.visibility = View.GONE
        binding?.include?.fastTaxiLayout?.visibility = View.VISIBLE
        binding?.include?.niceTaxiLayout?.visibility = View.VISIBLE
        binding?.include?.droneTaxiLayout?.visibility = View.VISIBLE
        binding?.include?.imageCancelOrder?.visibility = View.VISIBLE
        setBottomPanelData()
    }

    private fun setBottomPanelData() {
        completeRoad?.let {
            binding?.include?.fastTaxiPrice?.text =
                TaxiDataHelper.getFastCarTravelPrice(road = completeRoad!!, requireContext())
            binding?.include?.niceTaxiPrice?.text =
                TaxiDataHelper.getNiceCarTravelPrice(road = completeRoad!!, requireContext())
            binding?.include?.droneTaxiPrice?.text =
                TaxiDataHelper.getDroneCarTravelPrice(road = completeRoad!!, requireContext())

            binding?.include?.fastTaxiWaiting?.text =
                TaxiDataHelper.getFastCarWaiting(road = completeRoad!!, requireContext())
            binding?.include?.niceTaxiWaiting?.text =
                TaxiDataHelper.getNiceCarWaiting(road = completeRoad!!, requireContext())
            binding?.include?.droneTaxiWaiting?.text =
                TaxiDataHelper.getDroneCarWaiting(road = completeRoad!!, requireContext())
            onSelectLayout(binding?.include?.fastTaxiLayout, binding?.include?.fastTaxiPrice)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onSelectLayout(layout : View?, price : TextView?){
        binding?.include?.droneTaxiLayout?.setBackgroundColor(resources.getColor(R.color.white))
        binding?.include?.fastTaxiLayout?.setBackgroundColor(resources.getColor(R.color.white))
        binding?.include?.niceTaxiLayout?.setBackgroundColor(resources.getColor(R.color.white))
        layout?.setBackgroundColor(resources.getColor(R.color.selected_taxi_color))
        binding?.include?.buttonTaxiAction?.text = getString(R.string.order_for ) + " " + price?.text.toString()
    }

    private fun hideBottomTaxiPanel() {
        clearRoadOverlay()
        clearDestinationData()
        binding?.locationLabel?.visibility = View.VISIBLE
        binding?.include?.fastTaxiLayout?.visibility = View.GONE
        binding?.include?.niceTaxiLayout?.visibility = View.GONE
        binding?.include?.droneTaxiLayout?.visibility = View.GONE
        binding?.include?.imageCancelOrder?.visibility = View.GONE
    }

    private fun isRouteCompleted() : Boolean =
            binding?.map?.overlays?.contains(startMarker) == true && binding?.map?.overlays?.contains(destinationMarker) == true
                    && binding?.map?.overlays?.contains(roadOverlay) == true


    @SuppressLint("MissingPermission")
    private fun onWorkWithMap() : Boolean{
        return if(isLocationPermissionGranted()) {
            if(!isRouteDefined) {
                viewModel.onMapEvent()
                showMovableLabel()
                clearDestinationData()
                clearRoadOverlay()
            }
            if(!isLastLocationDefined)
                onLocationPermissionGranted()
            true
        }else{
            onWorkWithLocation()
            false
        }
    }

    private fun handleState(viewState: MapViewState?) =
            when(viewState){
                is MapViewState.Success.OnLocationDataLoaded -> onLocationDataLoaded(viewState.locationData)
                is MapViewState.OnLocationLabelSet -> onLocationLabelSet()
                is MapViewState.Success.OnLastLocationDefined -> setCurrentLocationToMapAndUI(locationData = viewState.locationData)
                is MapViewState.Error.UndefinedLastLocation -> onLastLocationUndefined(locationData = viewState.locationData)
                is MapViewState.Error.NetworkError -> onNetworkError()
                else -> print("what")
            }

    private fun onLocationDataLoaded(locationData: LocationData) {
        destinationLocation = locationData
        setUiWithDestination(locationData)
    }

    private fun setUiWithDestination(locationData: LocationData){
        setMarker(locationData)
        hideMovableLabel()
        setDestinationText(LocationDataConverter.convertLocationDataToString(locationData))
        if(startLocation != null){
            drawRoute(locationData)
        }
    }


    private fun hideMovableLabel() {
        binding?.locationLabel?.visibility = View.GONE
    }

    private fun showMovableLabel(){
        binding?.locationLabel?.visibility = View.VISIBLE
    }

    private fun drawRoute(destionation: LocationData) {
        val waypoints = ArrayList<GeoPoint>()
        waypoints.add(GeoPoint(startLocation!!.lat, startLocation!!.lon))
        waypoints.add(GeoPoint(destionation!!.lat, destionation!!.lon))

        val roadManager: RoadManager = OSRMRoadManager(context, "USER_AGENT")

        lifecycleScope.launch(Dispatchers.Default) {
            val road: Road = roadManager.getRoad(waypoints)
            withContext(Dispatchers.Main) {
                clearRoadOverlay()
                if (road.mStatus == Road.STATUS_OK) {
                    completeRoad = road
                    roadOverlay = RoadManager.buildRoadOverlay(road)
                    binding?.map?.overlays?.add(roadOverlay)
                    binding?.map?.invalidate()
                }
                else
                    context?.let{
                        Toast.makeText(it, getString(R.string.wrong_route_text), Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }


    private fun onLocationLabelSet() {
        binding?.map?.let{
            val centre = it.mapCenter
            viewModel.loadLocationData(Coordinates(centre.longitude, centre.latitude))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCurrentLocationToMapAndUI(locationData: LocationData) {
        clearStartLocationData()
        setStartLocationMarker(locationData)
        binding?.include?.taxiFromWhereField?.setText(LocationDataConverter.convertLocationDataToString(locationData))
        startLocation = locationData
        setMapCenter(Coordinates(lat = locationData.lat, lon = locationData.lon))
    }

    private fun setStartLocationMarker(locationData: LocationData){
        activity?.let {
            val startPoint = GeoPoint(locationData.lat, locationData.lon)
            startMarker = Marker(binding?.map)
            startMarker?.position = startPoint
            startMarker?.icon = ResourcesCompat.getDrawable(it.resources, R.drawable.ic_start_location, null)
            startMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            binding?.map?.overlays?.add(startMarker)
        }
    }


    private fun onLastLocationUndefined(locationData: LocationData) {
        activity?.let {
            Toast.makeText(
                requireActivity(),
                getString(R.string.text_last_location_undefined),
                Toast.LENGTH_LONG
            ).show()
        }
        setCurrentLocationToMapAndUI(locationData)
    }


    private fun onNetworkError() {
        activity?.let{
            (it as NetworkUser).showNetworkError()
        }
    }



    private fun setMapCenter(coordinates: Coordinates){
        if(mapController == null)
            prepareMap()
        val startPoint = GeoPoint(coordinates.lat, coordinates.lon)
        mapController?.setCenter(startPoint)
    }

    private fun prepareMap(){
        binding?.map?.setTileSource(TileSourceFactory.MAPNIK)
        mapController = binding?.map?.controller
        mapController?.setZoom(18.5)
    }


    private fun setDestinationText(destination: String) {
        binding?.include?.taxiWhereToField?.setText(destination)
    }

    private fun setMarker(locationData: LocationData) {
        activity?.let {
            clearDestinationData()
            val destinationPoint = GeoPoint(locationData.lat, locationData.lon)
            destinationMarker = Marker(binding?.map)
            destinationMarker?.position = destinationPoint
            destinationMarker?.icon = ResourcesCompat.getDrawable(it.resources, R.drawable.ic_label, null)
            destinationMarker?.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            binding?.map?.overlays?.add(destinationMarker)
        }
    }

    @SuppressLint("MissingPermission")
    private fun onWorkWithLocation() {
        when{
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> showLocationPermissionExplanationDialog()
            isRationaleShown -> showLocationPermissionDeniedDialog()
            else -> requestLocationPermission()
        }
    }

    private fun isLocationPermissionGranted() : Boolean{
        activity?.let {
            isPermissionGranted =  ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            return isPermissionGranted
        }
        return isPermissionGranted
    }

    private fun requestLocationPermission(){
        context?.let{
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun clearDestinationData() {
        if (destinationMarker != null)
            binding?.map?.overlays?.remove(destinationMarker)
        binding?.include?.taxiWhereToField?.setText("")
    }

    private fun clearStartLocationData() {
        if (startMarker != null)
            binding?.map?.overlays?.remove(startMarker)
        binding?.include?.taxiFromWhereField?.setText("")
        startLocation = null
    }

    private fun clearRoadOverlay() {
        if (roadOverlay != null && binding?.map?.overlays?.contains(roadOverlay) == true)
            binding?.map?.overlays?.remove(roadOverlay)
    }

    private fun showLocationPermissionExplanationDialog() {
        context?.let {
            AlertDialog.Builder(it)
                    .setMessage(getString(R.string.location_permission_explanation_text))
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        isRationaleShown = true
                        requestLocationPermission()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    private fun showLocationPermissionDeniedDialog() {
        context?.let {
            AlertDialog.Builder(it)
                    .setMessage(getString(R.string.location_permission_denied_text))
                    .setPositiveButton(R.string.ok) { dialog, _ ->
                        startActivity(
                                Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.parse("package:" + it.packageName)
                                )
                        )
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if(isPermissionGranted) {
            binding?.map?.onResume()
        }
        onWorkWithMap()
    }

    override fun onPause() {
        super.onPause()
        if(isPermissionGranted)
            binding?.map?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(isPermissionGranted)
            binding?.map?.onPause()
        binding = null
        editTextClickListener = null
    }

}