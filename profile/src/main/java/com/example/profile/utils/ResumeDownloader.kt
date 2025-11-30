package com.example.profile.utils

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

object ResumeDownloader {
    fun download(context: Context, url: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
                .setTitle("Резюме")
                .setDescription("Загрузка документа")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "resume.pdf")
            val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
        } catch (_: Exception) {
            // Ignore errors
        }
    }
}

