package com.dawidj.compass.data.repository

import com.dawidj.compass.util.CompassManager
import io.reactivex.Flowable
import javax.inject.Inject

interface CompassRepository {
    fun startCompass()
    fun stopCompass()
    fun getCompassStream(): Flowable<Float>
}

class CompassRepositoryImpl @Inject constructor(private val compassManager: CompassManager) : CompassRepository {
    override fun startCompass() = compassManager.start()
    override fun stopCompass() = compassManager.stop()
    override fun getCompassStream() = compassManager.getProcessor()
}