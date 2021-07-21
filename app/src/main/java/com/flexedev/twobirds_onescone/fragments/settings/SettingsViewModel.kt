package com.flexedev.twobirds_onescone.fragments.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class SettingsViewModel: ViewModel() {
    private var _defaultPinLocation = MutableLiveData(LatLng(-43.522448, 172.581017)) // Defaults to Jack Erskine
    private var _currentPinLocation = MutableLiveData<LatLng?>(null)

    val currentPinLocation: LiveData<LatLng?>
        get() = _currentPinLocation

    val defaultPinLocation: LiveData<LatLng>
        get() = _defaultPinLocation

    fun setCurrentPinLocation(location: LatLng) {
        _currentPinLocation.value = location
    }
}