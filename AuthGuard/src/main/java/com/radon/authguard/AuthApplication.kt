package com.radon.authguard

import android.app.Application
import com.radon.authguard.core.di.ApiModule
import com.radon.authguard.core.di.RepositoryModule
import com.radon.authguard.core.di.RetrofitModule
import com.radon.authguard.core.di.ViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AuthApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AuthApplication)
            modules(
                ApiModule,
                RepositoryModule,
                RetrofitModule,
                ViewModelModule
            )
        }
    }
}