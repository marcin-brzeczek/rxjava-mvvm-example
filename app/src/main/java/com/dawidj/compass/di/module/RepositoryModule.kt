package com.dawidj.compass.di.module

import com.dawidj.compass.data.repository.CompassRepository
import com.dawidj.compass.data.repository.CompassRepositoryImpl
import com.dawidj.compass.data.repository.LocationRepository
import com.dawidj.compass.data.repository.LocationRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {

    @Binds
    internal abstract fun bindCompassRepository(repository: CompassRepositoryImpl): CompassRepository

    @Binds
    internal abstract fun bindLocationRepository(repository: LocationRepositoryImpl): LocationRepository

}