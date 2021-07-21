package com.flexedev.twobirds_onescone.fragments.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.flexedev.twobirds_onescone.R
import com.flexedev.twobirds_onescone.TwoBirdsLiveRoomApplication
import com.flexedev.twobirds_onescone.data.entities.SconeWithRatings
import com.flexedev.twobirds_onescone.fragments.settings.SettingsViewModel
import com.flexedev.twobirds_onescone.helper.BitMapHelper
import com.flexedev.twobirds_onescone.viewModel.SconeViewModel
import com.flexedev.twobirds_onescone.viewModel.SconeViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class GoogleMapFragment : Fragment(), OnMapReadyCallback  {
    private val viewModel: SconeViewModel by activityViewModels {

        SconeViewModelFactory((activity?.application as TwoBirdsLiveRoomApplication).repository)
    }
    private lateinit var mMap: GoogleMap

    private lateinit var sharedPreferences: SharedPreferences
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    val hasLocationPermissions get() = requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_google_map, container, false)

        // Inflate the layout for this fragment
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        loadData()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * This function is called when the map is loaded and ready.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        updateLocation()
        var sconesList: List<SconeWithRatings>

        viewModel.scones.observe(viewLifecycleOwner) { newScones ->
            sconesList = newScones
            populateMapWithScones(sconesList)
        }

    }

    @SuppressLint("MissingPermission")
    private fun updateLocation(): LatLng {
        var newLocation = LatLng(Double.NaN, Double.NaN)
        if (hasLocationPermissions) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    newLocation = updateLocationHelper(location)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15.0f))
                    mMap.addMarker(MarkerOptions().position(newLocation).title("Your Position").icon(
                        BitMapHelper().bitMapFromVector(requireContext(), R.drawable.ic_location_icon)))
                }
            }
        } else {
            val currentLat = sharedPreferences.getString("defaultLat", null)
            val currentLng = sharedPreferences.getString("defaultLng", null)

            newLocation = if (currentLat != null && currentLng != null) {
                LatLng(currentLat.toDouble(), currentLng.toDouble())
            } else {
                settingsViewModel.defaultPinLocation.value!!
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15.0f))
            mMap.addMarker(MarkerOptions().position(newLocation).title("Your Position").icon(
                BitMapHelper().bitMapFromVector(requireContext(), R.drawable.ic_location_icon)))

        }
        return newLocation
    }

    private fun updateLocationHelper(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    private fun populateMapWithScones(sconesList : List<SconeWithRatings>) {

        Log.d(viewModel.scones.toString(),"HI")
        for (scone1 in sconesList) {
            Log.d(scone1.scone.longitude,"LONG")
            Log.d(scone1.scone.latitude,"LAT")
            val sconeLocation = LatLng(scone1.scone.latitude.toDouble(),scone1.scone.longitude.toDouble())
            val titleString = scone1.scone.sconeBusiness + " - " + scone1.scone.sconeName
//            mMap.addMarker(MarkerOptions().position(sconeLocation).title(titleString).snippet(scone1.scone.id.toString()))
            val newMarker = MarkerOptions().position(sconeLocation).title(titleString).snippet(scone1.scone.id.toString()).icon(
                BitMapHelper().bitMapFromVector(requireContext(), R.drawable.ic_anyconv_com__2birds1scone__2_))
            mMap.addMarker(newMarker)


        }
        mMap.setOnMarkerClickListener { marker ->
            Log.d(marker.title,"YOOO")
            if (!marker.snippet.isNullOrBlank()) {
                val sconeId = marker.snippet.toInt()
                val action = MapFragmentDirections.actionMapFragmentToUpdateRatingFragment(sconeId)
                findNavController().navigate(action)
            }
            true
        }

    }



    companion object {
        fun newInstance(): GoogleMapFragment = GoogleMapFragment()
    }

    private fun loadData() {
        sharedPreferences =
            requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    }
}