package com.suvairin.showplayer

import android.Manifest
import android.app.DownloadManager
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.suvairin.showplayer.adapter.BroadcastAdapter
import com.suvairin.showplayer.databinding.ActivityMainBinding
import com.suvairin.showplayer.downloader.DownloadCompletedReceiver

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var broadcastAdapter: BroadcastAdapter // Объект Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        broadcastAdapter = BroadcastAdapter()
        setContentView(binding.root)
        val manager = LinearLayoutManager(this) // LayoutManager
        var downloadCompletedReceiver: DownloadCompletedReceiver = DownloadCompletedReceiver(broadcastAdapter)
        registerReceiver(downloadCompletedReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        requestPermissions()
        binding.recyclerView.layoutManager = manager // Назначение LayoutManager для RecyclerView
        binding.recyclerView.adapter = broadcastAdapter // Назначение адаптера для RecyclerView



    }

    private fun hasWriteExternalStoragePermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun hasReadExternalStoragePermission() =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        var permissionRequest = mutableListOf<String>()
        if(!hasWriteExternalStoragePermission()) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if(!hasReadExternalStoragePermission()) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if(permissionRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionRequest.toTypedArray(), 0)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0 && grantResults.isNotEmpty()) {
            for(i in grantResults.indices) {
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("0000", "${permissions[i]} granted.")
                    App.getInstance().initPlaylistService()
                }
            }
        }
    }
}

