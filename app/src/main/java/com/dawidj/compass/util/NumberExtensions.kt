package com.dawidj.compass.util

fun Double.validateLatitude() = this >= -90 && this <= 90
fun Double.validateLongitude() = this >= -180 && this <= 180