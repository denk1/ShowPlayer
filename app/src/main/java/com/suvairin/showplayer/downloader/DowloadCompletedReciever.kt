package com.suvairin.showplayer.downloader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.suvairin.showplayer.App
import com.suvairin.showplayer.adapter.BroadcastAdapter

class DownloadCompletedReceiver(
    private val broadcastAdapter: BroadcastAdapter
): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if(id != -1L) {
                println("Download with ID $id finished")
                App.getInstance().readData()
                broadcastAdapter.data = App.getInstance().getBroadcast
                val test = 2 + 2

            }
        }
    }
}