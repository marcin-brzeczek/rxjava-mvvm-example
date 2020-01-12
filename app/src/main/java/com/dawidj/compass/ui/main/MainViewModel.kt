package com.dawidj.compass.ui.main

import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.dawidj.compass.data.repository.CompassRepository
import com.dawidj.compass.data.repository.LocationRepository
import com.dawidj.compass.ui.base.BaseViewModel
import com.dawidj.compass.util.observeOnMainThread
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val compassRepository: CompassRepository,
    private val locationRepository: LocationRepository
) : BaseViewModel() {

    val isLocationEnabled = MutableLiveData<Boolean>()
    val currentLocation = MutableLiveData<Location>()
    val azimuthData = MutableLiveData<Float>()

    fun getLocationStatus() {
        locationRepository.isLocationEnabled()
            .subscribeOn(Schedulers.io())
            .observeOnMainThread()
            .subscribeBy(
                onSuccess = { isEnabled -> isLocationEnabled.value = isEnabled },
                onError = { handleFailure(it) }
            )
            .collect()
    }

    fun getCurrentLocation() {
        locationRepository.getUserLocation()
            .subscribeOn(Schedulers.io())
            .observeOnMainThread()
            .subscribeBy(
                onSuccess = { location -> currentLocation.value = location },
                onError = { handleFailure(it) }
            )
            .collect()
    }

    fun stopCompass() = compassRepository.stopCompass()

    fun startCompass() = compassRepository.startCompass()

    fun startCompassEvents() {
        compassRepository.getCompassStream()
            .subscribeOn(Schedulers.computation())
            .observeOnMainThread()
            .subscribeBy(
                onNext = { azimuthData.value = it},
                onError = { handleFailure(it) }
            )
            .collect()
    }
}