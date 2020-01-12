package com.dawidj.compass.ui.main

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dawidj.compass.R
import com.dawidj.compass.ui.base.BaseActivity
import com.dawidj.compass.util.*
import icepick.State
import kotlinx.android.synthetic.main.activity_main.*

private const val LOCATION_PERMISSIONS_REQUEST_CODE = 100
private val LOCATION_PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
private const val NO_ERROR = ""

class MainActivity : BaseActivity() {

    override val layoutResId: Int = R.layout.activity_main

    private lateinit var viewModel: MainViewModel

    private var currentAzimuth = 0F
    private var isLatitudeCorrect = false
    private var isLongitudeCorrect = false
    private var currentLocation: Location? = null

    @State
    @JvmField
    protected var currentDirection = -1f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel(viewModelFactory) {
            failure(failure, ::handleError)
            observe(isLocationEnabled, ::handleLocationStatus)
            observe(currentLocation, ::handleCurrentLocation)
            observe(azimuthData, ::handleCompassData)
        }

        initListeners()
        getUserLocation()
        if (currentDirection != -1f) setDirectionArrow(currentDirection)
    }

    private fun initListeners() {
        lng.addTextChangedListener(longitudeTextWatcher)
        lat.addTextChangedListener(latitudeTextWatcher)
        lng.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) view.hideKeyboard()
        }
        background.onClick {
            currentFocus?.hideKeyboard()
            compassNeedle.requestFocus()
        }
        addDirection.onClick { onAddDirectionClick() }
    }

    private fun setDirectionArrow(bearing: Float) {
        val radius = compassLayout.width.div(2)
        val arrowX =
            (compassLayout.pivotX + Math.cos((bearing - 90) * Math.PI / 180F) * radius.times(0.95)).toFloat() - (arrow.width / 2)
        val arrowY =
            (compassLayout.pivotY + Math.sin((bearing - 90) * Math.PI / 180F) * radius.times(0.95)).toFloat() - (arrow.height / 2)

        with(arrow) {
            x = arrowX
            y = arrowY
            rotation = bearing
            visible()
        }
    }

    private fun handleLocationStatus(isEnabled: Boolean?) {
        isEnabled?.let { if (it) startLocatingUser() else showLocalizationOffDialog() }
    }

    private fun handleCurrentLocation(currentLocation: Location?) {
        this.currentLocation = currentLocation
    }

    private fun handleError(throwable: Throwable?) {
        throwable?.printStackTrace()
    }

    private fun handleCompassData(azimuth: Float?) {
        azimuth?.let {
            val animation = RotateAnimation(
                currentAzimuth,
                +azimuth,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            ).apply {
                duration = 150
                fillAfter = true
            }

            currentAzimuth = +azimuth
            direction.text = getString(R.string.direction, azimuth.toInt(), getDirectionIndicator(azimuth.toInt()))
            compassNeedle.startAnimation(animation)
        }
    }

    private fun onAddDirectionClick() {
        if (isCorrectLatLngProvided()) {
            currentFocus?.hideKeyboard()
            currentDirection = getBearing()
            setDirectionArrow(currentDirection)
        } else {
            arrow.gone()
        }
    }

    private fun startLocatingUser() = viewModel.getCurrentLocation()

    private fun showLocalizationOffDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.localization_dialog_title))
            .setMessage(getString(R.string.localization_dialog_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .create()
            .show()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopCompass()
    }

    override fun onStart() {
        super.onStart()
        viewModel.startCompass()
        viewModel.startCompassEvents()
    }

    private fun getDirectionIndicator(azimuth: Int): String =
        when (azimuth) {
            in 23..67 -> getString(R.string.north_east)
            in 68..112 -> getString(R.string.east)
            in 113..157 -> getString(R.string.south_east)
            in 158..201 -> getString(R.string.south)
            in 201..246 -> getString(R.string.south_west)
            in 248..291 -> getString(R.string.west)
            in 293..336 -> getString(R.string.north_west)
            else -> getString(R.string.north)
        }

    private fun getBearing(): Float {
        val from = Location(LocationManager.GPS_PROVIDER)
            .apply {
                latitude = currentLocation!!.latitude
                longitude = currentLocation!!.longitude
            }
        val to = Location(LocationManager.GPS_PROVIDER)
            .apply {
                latitude = lat.text.toString().toDouble()
                longitude = lng.text.toString().toDouble()
            }

        return from.bearingTo(to)
    }

    private val latitudeTextWatcher = object : SimpleTextWatcher() {
        override fun afterTextChanged(text: Editable?) {
            if (text.isNullOrBlank()) {
                isLatitudeCorrect = false
            } else {
                val latitude = text.toString().toDoubleOrNull()
                latitude?.let {
                    latLayout.error = if (it.validateLatitude()) {
                        isLatitudeCorrect = true
                        NO_ERROR
                    } else {
                        isLatitudeCorrect = false
                        getString(R.string.incorrect_latitude)
                    }
                }
            }
        }
    }

    private val longitudeTextWatcher = object : SimpleTextWatcher() {
        override fun afterTextChanged(text: Editable?) {
            if (text.isNullOrBlank()) {
                isLongitudeCorrect = false
            } else {
                val longitude = text.toString().toDoubleOrNull()
                longitude?.let {
                    lngLayout.error = if (it.validateLongitude()) {
                        isLongitudeCorrect = true
                        NO_ERROR
                    } else {
                        isLongitudeCorrect = false
                        getString(R.string.incorrect_longitude)
                    }
                }
            }
        }
    }

    private fun isCorrectLatLngProvided(): Boolean {
        return when {
            !isLongitudeCorrect || !isLatitudeCorrect -> {
                Toast.makeText(this, getString(R.string.latitude_longitude_error), Toast.LENGTH_SHORT).show()
                false
            }
            currentLocation == null -> {
                showLocalizationOffDialog()
                Toast.makeText(this, getString(R.string.location_error), Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun isLocationPermissionGranted(): Boolean =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getUserLocation()
        }
    }

    private fun getUserLocation() {
        if (isLocationPermissionGranted())
            viewModel.getLocationStatus()
        else
            ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, LOCATION_PERMISSIONS_REQUEST_CODE)
    }
}
