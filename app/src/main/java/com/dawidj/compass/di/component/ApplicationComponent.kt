package com.dawidj.compass.di.component

import android.content.Context
import com.dawidj.compass.CompassApplication
import com.dawidj.compass.di.module.ActivityModuleBuilder
import com.dawidj.compass.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    ActivityModuleBuilder::class])
interface ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): ApplicationComponent
    }

    fun inject(application: CompassApplication)
}