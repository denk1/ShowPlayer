package com.suvairin.showplayer

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.suvairin.showplayer.model.PlaylistService

class App: Application() {
    private lateinit var playlistService : PlaylistService
    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        if(checkPermissions())
            initPlaylistService()

    }

    public fun initPlaylistService() {
        playlistService = PlaylistService(App.applicationContext())
    }

    private fun checkPermissions():Boolean {
        return hasReadExternalStoragePermission() && hasWriteExternalStoragePermission()
    }

    private fun hasWriteExternalStoragePermission() =
        ActivityCompat.checkSelfPermission(applicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun hasReadExternalStoragePermission() =
        ActivityCompat.checkSelfPermission(applicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    public fun readData() {
        playlistService.getData()
    }

    val getBroadcast
        get() = playlistService.getDataList

    companion object {
        private var instance: App? = null

        public fun getInstance(): App {
            return instance!!
        }

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }
}