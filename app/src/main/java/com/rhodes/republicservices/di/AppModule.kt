package com.rhodes.republicservices.di

import com.rhodes.republicservices.ui.DriverViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel { DriverViewModel(repository = get()) }
}