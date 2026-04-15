package com.template.app

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class TemplateApplication : Application() {

    @Inject lateinit var imageLoader: Provider<ImageLoader>

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        SingletonImageLoader.setSafe { _ -> imageLoader.get() }
    }
}
