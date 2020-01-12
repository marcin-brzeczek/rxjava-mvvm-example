package com.dawidj.compass.di.injector

import android.app.Activity
import android.os.Bundle
import com.dawidj.compass.CompassApplication
import com.dawidj.compass.di.component.ApplicationComponent
import com.dawidj.compass.util.SimpleActivityLifecycleCallbacks
import dagger.android.AndroidInjection
import dagger.android.support.HasSupportFragmentInjector

class AppInjector {

    companion object {

        fun init(application: CompassApplication, applicationComponent: ApplicationComponent) {
            applicationComponent.inject(application)
            application.registerActivityLifecycleCallbacks(object : SimpleActivityLifecycleCallbacks() {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    handleActivity(activity)
                }
            })
        }

        private fun handleActivity(activity: Activity) {
            if (activity is HasSupportFragmentInjector) AndroidInjection.inject(activity)
        }
    }
}