package com.dawidj.compass

import android.app.Activity
import android.app.Application
import com.dawidj.compass.di.component.ApplicationComponent
import com.dawidj.compass.di.component.DaggerApplicationComponent
import com.dawidj.compass.di.injector.AppInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import javax.inject.Inject

class CompassApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var mDispatchingAndroidActivityInjector: DispatchingAndroidInjector<Activity>

    private lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()

        applicationComponent = DaggerApplicationComponent.builder()
            .context(this)
            .build()

        AppInjector.init(this, applicationComponent)

        initTimber()

        RxJavaPlugins.setErrorHandler {
            if (it is UndeliverableException) Timber.d("UndeliverableException caught: ${it.localizedMessage}")
        }
    }

    private fun initTimber() = if(BuildConfig.USE_TIMBER) Timber.plant(Timber.DebugTree()) else Unit

    override fun activityInjector(): AndroidInjector<Activity> = mDispatchingAndroidActivityInjector

}