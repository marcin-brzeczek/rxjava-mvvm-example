package com.dawidj.compass.data.repository

import android.location.Location
import com.dawidj.compass.util.LocationManager
import io.reactivex.Single
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val locationManager: LocationManager) : LocationRepository {
    override fun isLocationEnabled(): Single<Boolean> = locationManager.isLocationEnabled()
    override fun getUserLocation(): Single<Location> = locationManager.getLastKnownLocation()
}

interface LocationRepository {
    fun isLocationEnabled(): Single<Boolean>
    fun getUserLocation(): Single<Location>
}