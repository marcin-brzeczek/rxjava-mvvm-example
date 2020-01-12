package com.dawidj.compass.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    val failure = MutableLiveData<Throwable>()

    fun handleFailure(throwable: Throwable) {
        failure.value = throwable
    }

    override fun onCleared() = compositeDisposable.clear()

    fun Disposable.collect() = compositeDisposable.add(this)

}