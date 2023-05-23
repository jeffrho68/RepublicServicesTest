package com.rhodes.republicservices

import android.app.Application
import com.rhodes.republicservices.di.appModule
import com.rhodes.republicservices.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class RepublicServicesApplication : Application()  {

    override fun onCreate() {
        super.onCreate()
        //Init Koin DI
        startKoin {
            androidLogger()
            androidContext(this@RepublicServicesApplication)
            modules(dataModule, appModule)
        }
    }
}