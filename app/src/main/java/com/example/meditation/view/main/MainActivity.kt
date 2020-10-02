package com.example.meditation.view.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.meditation.R
import com.example.meditation.service.MusicService
import com.example.meditation.service.MusicServiceHelper
import com.example.meditation.util.FragmentTag
import com.example.meditation.util.NotificationHelper
import com.example.meditation.util.PlayStatus
import com.example.meditation.view.dialog.LevelSelectDialog
import com.example.meditation.view.dialog.ThemeSelectDialog
import com.example.meditation.view.dialog.TimeSelectDialog
import com.example.meditation.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    //private lateinit var viewModel: MainViewModel
    private val viewModel: MainViewModel by viewModel()

    //private var musicServiceHelper: MusicServiceHelper? = null
    private val musicServiceHelper: MusicServiceHelper by inject()
    //private var notificationHelper: NotificationHelper? = null
    private val notificationHelper: NotificationHelper by inject()

    private val mainFragment: MainFragment by inject()
    private val levelSelectDialog: LevelSelectDialog by inject()
    private val themeSelectDialog: ThemeSelectDialog by inject()
    private val timeSelectDialog: TimeSelectDialog by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(
                    R.id.screen_container,
                    mainFragment
                )
                .commit()
        }

        //viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        observeViewModel()

        btmNavi.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.item_select_level -> {
                    levelSelectDialog.show(supportFragmentManager,FragmentTag.LEVEL_SELECT.name)
                    true
                }
                R.id.item_select_theme -> {
                    themeSelectDialog.show(supportFragmentManager, FragmentTag.TIME_SELECT.name)
                    true
                }
                R.id.item_select_time -> {
                    timeSelectDialog.show(supportFragmentManager,FragmentTag.TIME_SELECT.name)
                    true
                }
                else -> { false}
            }
        }

        //musicServiceHelper = MusicServiceHelper(this)
        musicServiceHelper.bindService()

        //notificationHelper = NotificationHelper(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        musicServiceHelper.stopBgm()
        finish()
    }

    override fun onResume() {
        super.onResume()
        notificationHelper.cancelNotification()
    }

    override fun onPause() {
        super.onPause()
        notificationHelper.startNotification()
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationHelper.cancelNotification()
    }

    private fun observeViewModel() {
        viewModel.playStatus.observe(this, Observer { status ->
            when (status) {
                PlayStatus.BEFORE_START -> {
                    btmNavi.visibility = View.VISIBLE
                }
                PlayStatus.ON_START -> {
                    btmNavi.visibility = View.INVISIBLE
                }
                PlayStatus.RUNNING -> {
                    btmNavi.visibility = View.INVISIBLE
                    musicServiceHelper.startBgm()
                }
                PlayStatus.PAUSE -> {
                    btmNavi.visibility = View.INVISIBLE
                    musicServiceHelper.stopBgm()
                }
                PlayStatus.END -> {
                    btmNavi.visibility = View.VISIBLE
                    musicServiceHelper.stopBgm()
                    musicServiceHelper.ringFinalGong()
                }
            }
        })
        viewModel.volume.observe(this, Observer { volume ->
            musicServiceHelper.setVolume(volume!!)
        })
    }
}