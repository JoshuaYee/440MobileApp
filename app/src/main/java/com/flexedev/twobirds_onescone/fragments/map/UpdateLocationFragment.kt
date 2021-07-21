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
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.flexedev.twobirds_onescone.R
import com.flexedev.twobirds_onescone.viewModel.RatingViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class UpdateLocationFragment : DialogFragment(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private lateinit var mMap: GoogleMap
    private val ratingViewModel: RatingViewModel by activityViewModels()
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var markerOptions: MarkerOptions
    private var marker: Marker? = null

    // Location & permissions related
    val hasLocationPermissions get() = requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        loadData()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Update user's location before we do anything with the maps
        return inflater.inflate(R.layout.dialog_add_location, container, false)
    }

    override fun onResume() {
        super.onResume()
        val params = dialog!!.window!!.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog!!.window!!.attributes = params
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tickIcon = view.findViewById<ImageView>(R.id.dialog_tick_icon)
        tickIcon.setOnClickListener {
            dismiss()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    /**
     * This function is called when the map is loaded and ready.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("GOOGLE MAPS", "Map is ready to go")
        mMap = googleMap

        if (ratingViewModel.currentSconeLocation.value != null) {
            updatePinLocation(null)
        } else {
            updateLocation()
        }

        mMap.setOnMapClickListener {
            onMapClick(it)
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation(): LatLng {
        var newLocation = LatLng(Double.NaN, Double.NaN)
        if (hasLocationPermissions) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    newLocation = updateLocationHelper(location)

                    updatePinLocation(newLocation)
                }
            }
        } else {
            val currentLat = sharedPreferences.getString("defaultLat", null)
            val currentLng = sharedPreferences.getString("defaultLng", null)

            newLocation = if (currentLat != null && currentLng != null) {
                LatLng(currentLat.toDouble(), currentLng.toDouble())
            } else {
                ratingViewModel.defaultPinLocation.value!!
            }
            updatePinLocation(newLocation)
        }
        return newLocation
    }

    private fun updatePinLocation(newLocation: LatLng?) {

        if (newLocation != null) {
            ratingViewModel.setCurrentSconeLocation(newLocation)
        } // Otherwise just use the default location in the view model

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ratingViewModel.currentSconeLocation.value!!, 15.0f))

        markerOptions = createMarkerOptions(ratingViewModel.currentSconeLocation.value!!)

        marker = mMap.addMarker(markerOptions)

        mMap.setOnMarkerDragListener(this)
    }

    private fun createMarkerOptions(latLng: LatLng): MarkerOptions {
        return MarkerOptions()
                .position(latLng)
                .title("Hold and drag to place marker")
                .draggable(true)
    }



    override fun onMarkerDragStart(p0: Marker) {
        /**
         * Called when user starts dragging the pin
         */
    }


    override fun onMarkerDrag(p0: Marker) {
        /**
         * Called as user is dragging pin
         */
    }

    /**
     * Called when user drops the pin
     */
    override fun onMarkerDragEnd(p0: Marker) {
        Toast.makeText(activity, p0.position.toString(), Toast.LENGTH_SHORT).show()
        ratingViewModel.setCurrentSconeLocation(p0.position)
        saveData(p0)
    }

    private fun loadData() {
        sharedPreferences = requireContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
    }

    private fun saveData(p0: Marker) {
        val selectedLatLong: LatLng = p0.position
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.apply {
            putString("STRING_KEY", selectedLatLong.toString())
        }.apply()
        Toast.makeText(requireContext(), "Location Selected", Toast.LENGTH_SHORT).show()
    }

    private fun updateLocationHelper(location: Location): LatLng {
        return LatLng(location.latitude, location.longitude)
    }

    companion object {
        fun newInstance(): GoogleMapFragment = GoogleMapFragment()
    }

    private fun onMapClick(latLng: LatLng) {
        Log.d("GOOGLE MAP", "Map was clicked!")
        marker!!.remove()
        ratingViewModel.setCurrentSconeLocation(latLng)
        marker = mMap.addMarker(createMarkerOptions(latLng))
    }
}