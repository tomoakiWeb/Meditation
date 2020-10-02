package com.example.meditation.di

import android.app.Application
import com.example.meditation.model.UserSettingsRepository
import com.example.meditation.service.MusicServiceHelper
import com.example.meditation.util.NotificationHelper
import com.example.meditation.view.dialog.LevelSelectDialog
import com.example.meditation.view.dialog.ThemeSelectDialog
import com.example.meditation.view.dialog.TimeSelectDialog
import com.example.meditation.view.main.MainFragment
import com.example.meditation.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val appModule = module {
    factory { MainFragment() }
    factory { LevelSelectDialog() }
    factory { ThemeSelectDialog() }
    factory { TimeSelectDialog() }
    factory { NotificationHelper(get()) }
    factory { MusicServiceHelper(get()) }
    factory { UserSettingsRepository() }

    viewModel { MainViewModel(androidContext() as Application) }

}