package com.flexedev.twobirds_onescone.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class RatingViewModel: ViewModel() {
    private var _defaultPinLocation = MutableLiveData(LatLng(-43.522448, 172.581017)) // Defaults to Jack Erskine
    private var _currentPinLocation = MutableLiveData<LatLng?>(null)

    private var _currentSconeLocation = MutableLiveData<LatLng?>(null)

    val currentPinLocation: LiveData<LatLng?>
        get() = _currentPinLocation


    val currentSconeLocation: LiveData<LatLng?>
        get() = _currentSconeLocation


    val defaultPinLocation: LiveData<LatLng>
        get() = _defaultPinLocation

    fun setCurrentPinLocation(location: LatLng?) {
        _currentPinLocation.value = location
    }

    fun setCurrentSconeLocation(location: LatLng?) {
        _currentSconeLocation.value = location
    }
}