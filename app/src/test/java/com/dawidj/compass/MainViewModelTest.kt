package com.dawidj.compass

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.dawidj.compass.data.repository.CompassRepository
import com.dawidj.compass.data.repository.LocationRepository
import com.dawidj.compass.ui.main.MainViewModel
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.random.Random

@RunWith(JUnit4::class)
class MainViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    private val compassRepository: CompassRepository = mock()
    private val locationRepository: LocationRepository = mock()

    private val locationEnabledObserver: Observer<Boolean> = mock()
    private val locationObserver: Observer<Location> = mock()
    private val azimuthObserver: Observer<Float> = mock()

    @Before
    fun setUp() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        viewModel = MainViewModel(compassRepository, locationRepository)
    }

    @Test
    fun Test_LocaionShouldBeEnabled() {
        whenever(locationRepository.isLocationEnabled()).thenReturn(Single.just(true))
        viewModel.isLocationEnabled.observeForever(locationEnabledObserver)

        viewModel.getLocationStatus()

        assert(viewModel.isLocationEnabled.value == true)
    }

    @Test
    fun Test_LocaionShouldNotBeEnabled() {
        whenever(locationRepository.isLocationEnabled()).thenReturn(Single.just(false))
        viewModel.isLocationEnabled.observeForever(locationEnabledObserver)

        viewModel.getLocationStatus()

        assert(viewModel.isLocationEnabled.value == false)
    }

    @Test
    fun Test_ShouldReturnRandomLocation() {
        whenever(locationRepository.getUserLocation()).thenReturn(Single.just(randomLocation))
        viewModel.currentLocation.observeForever(locationObserver)

        viewModel.getCurrentLocation()

        assert(viewModel.currentLocation.value == randomLocation)
    }


    @Test
    fun Test_ShouldReturnCompassAzimuth() {
        whenever(compassRepository.getCompassStream()).thenReturn(Flowable.just(randomAzimuth))
        viewModel.azimuthData.observeForever(azimuthObserver)

        viewModel.startCompassEvents()

        assert(viewModel.azimuthData.value == randomAzimuth)
    }

    private val randomAzimuth = Random.nextFloat()

    private val randomLocation = Location("")
        .apply {
            latitude = Random.nextDouble()
            longitude = Random.nextDouble()
        }
}