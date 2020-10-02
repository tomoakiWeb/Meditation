package com.example.meditation.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Binder
import android.os.IBinder
import android.view.Display
import com.example.meditation.MyApplication
import com.example.meditation.R
import com.example.meditation.model.UserSettingsRepository
import com.example.meditation.util.NO_BGM
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.Extractor
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.RawResourceDataSource

class MusicService : Service() {

    private val binder = MusicBinder()

    inner class MusicBinder: Binder() {
        fun getService(): MusicService{
            return  this@MusicService
        }
    }

    private var bellsPlayer: SimpleExoPlayer? = null
    private var bgmPlayer: SimpleExoPlayer? = null

    private var rawResourceDataSourceBell: RawResourceDataSource? = null
    private var rawResourceDataSourceBgm: RawResourceDataSource? = null

    private var soundPool: SoundPool? = null
    private var soundIdGoing: Int = 0

    private lateinit var audioManager: AudioManager
    private var maxVolume: Int = 0

    override fun onBind(intent: Intent): IBinder {
        bellsPlayer = ExoPlayerFactory.newSimpleInstance(this,DefaultTrackSelector())
        bgmPlayer = ExoPlayerFactory.newSimpleInstance(this,DefaultTrackSelector())
        rawResourceDataSourceBgm = RawResourceDataSource(this)
        rawResourceDataSourceBell = RawResourceDataSource(this)
        initSoundPool()
        initAudioManager()
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        soundPool?.release()
        return super.onUnbind(intent)
    }

    fun startBgm() {
        val userSettingRepository = UserSettingsRepository()
        val bellsSoundId = when(userSettingRepository.loadUserSettings().levelId){
            0 -> R.raw.bells_easy
            1 -> R.raw.bells_normal
            2 -> R.raw.bells_mid
            3 -> R.raw.bells_advanced
            else -> 0
        }
        val themeSoundId = userSettingRepository.loadUserSettings().themeSoundId
        bellsPlayer?.let { startSound(bellsPlayer!!, bellsSoundId, rawResourceDataSourceBell!!) }
        if (themeSoundId != NO_BGM) bgmPlayer?.let { startSound(bgmPlayer!!, themeSoundId, rawResourceDataSourceBgm!!) }

    }

    private fun startSound(exoPlayer: SimpleExoPlayer, soundId: Int, rawResourceDataSource: RawResourceDataSource) {
        val dataSourceUri = RawResourceDataSource.buildRawResourceUri(soundId)
        val dataSpec = DataSpec(dataSourceUri)
        rawResourceDataSource.open(dataSpec)
        val dataSourceFactory = DataSource.Factory { return@Factory rawResourceDataSource }
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .setExtractorsFactory(Mp3Extractor.FACTORY)
            .createMediaSource(dataSourceUri)
        exoPlayer.apply {
            prepare(mediaSource)
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }

    fun stopBgm() {
        bellsPlayer?.stop()
        bgmPlayer?.stop()
    }

    fun setVolume(volume: Int) {
        val adjustedVolume = (maxVolume.toDouble() * (volume.toDouble() / 100.toDouble())).toInt()
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, adjustedVolume, 0)
    }

    fun ringFinalGong() {
        soundPool?.play(soundIdGoing, 1.0f,1.0f,0,0,1.0f)
    }

    private fun releaseExoPlayer(){
        bellsPlayer?.release()
        bgmPlayer?.release()
        rawResourceDataSourceBell?.close()
        rawResourceDataSourceBgm?.close()
    }

    private fun initAudioManager() {
        audioManager = MyApplication.appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
    }

    private fun initSoundPool() {
        soundPool = SoundPool.Builder().setAudioAttributes(AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build())
            .setMaxStreams(1)
            .build()
        soundIdGoing = soundPool?.load(this,R.raw.gong_meiso_end, 1)!!

    }
}
