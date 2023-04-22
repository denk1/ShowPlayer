package com.suvairin.showplayer.downloader

interface Downloader {
    fun downloadFile(url: String): Long
}