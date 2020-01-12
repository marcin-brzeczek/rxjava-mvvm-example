package com.dawidj.compass.util

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers

fun <T> Single<T>.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())
fun <T> Flowable<T>.observeOnMainThread() = this.observeOn(AndroidSchedulers.mainThread())