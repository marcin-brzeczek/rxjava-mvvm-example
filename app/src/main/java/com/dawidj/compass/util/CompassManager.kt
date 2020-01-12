package com.dawidj.compass.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.reactivex.Flowable
import io.reactivex.processors.PublishProcessor
import javax.inject.Inject

private const val  alpha = 0.97f

class CompassManager @Inject constructor(context: Context) : SensorEventListener {

    private val processor = PublishProcessor.create<SensorEvent>()

    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometerSensor: Sensor
    private val magneticSensor: Sensor

    private val gravity = FloatArray(3)
    private val geometric = FloatArray(3)
    private val R = FloatArray(9)
    private val I = FloatArray(9)

    init {
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onSensorChanged(event: SensorEvent) = processor.onNext(event)

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    fun start() {
        sensorManager.registerListener(
            this, accelerometerSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
        sensorManager.registerListener(
            this, magneticSensor,
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    fun stop() = sensorManager.unregisterListener(this)

    fun getProcessor(): Flowable<Float> =
        processor
            .map { event ->
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                    gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                    gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]
                }

                if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    geometric[0] = alpha * geometric[0] + (1 - alpha) * event.values[0]
                    geometric[1] = alpha * geometric[1] + (1 - alpha) * event.values[1]
                    geometric[2] = alpha * geometric[2] + (1 - alpha) * event.values[2]
                }
            }
            .filter { SensorManager.getRotationMatrix(R, I, gravity, geometric) }
            .map {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(R, orientation)
                val degrees = Math.toDegrees(orientation[0].toDouble()).toFloat()
                val azimuth = (degrees + 360) % 360
                azimuth
            }
}
