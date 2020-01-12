package com.dawidj.compass.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dagger.android.AndroidInjection
import icepick.Icepick
import javax.inject.Inject


abstract class BaseActivity : AppCompatActivity() {

	@Inject
	protected lateinit var viewModelFactory: ViewModelProvider.Factory

    abstract val layoutResId: Int

	override fun onCreate(savedInstanceState: Bundle?) {
		AndroidInjection.inject(this)
		super.onCreate(savedInstanceState)
		setContentView(layoutResId)
        Icepick.restoreInstanceState(this, savedInstanceState)
	}

	override fun onSaveInstanceState(outState: Bundle?) {
		super.onSaveInstanceState(outState)
        outState?.let { Icepick.saveInstanceState(this, it) }
	}
}