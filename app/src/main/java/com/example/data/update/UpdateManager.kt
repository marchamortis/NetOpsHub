package com.example.data.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.data.model.AppVersionResponse
import com.example.data.remote.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * UpdateManager (v2.0.1 - "Online app")
 *
 * The app is now self-updating:
 *  1. On every launch it asks the Cloudflare Worker for the latest published
 *     release (GET /api/v1/app/version).
 *  2. If the server version_code is newer than the installed one, the app shows
 *     a "new version" dialog (changelog EN/FA) directly inside the app.
 *  3. One tap downloads the new APK via the system DownloadManager and triggers
 *     the package installer - no manual GitHub download required.
 *
 * Content updates (servers, articles, phone book, infographics data) continue
 * to stream automatically through the authenticated /sync/all channel, so most
 * changes reach users with ZERO reinstall.
 */
object UpdateManager {

    const val CURRENT_VERSION_CODE = 201
    const val CURRENT_VERSION_NAME = "2.0.1"

    data class UpdateInfo(
        val versionCode: Int,
        val versionName: String,
        val changelogEn: String,
        val changelogFa: String,
        val apkUrl: String,
        val exeUrl: String,
        val mandatory: Boolean
    ) {
        val hasUpdate: Boolean get() = versionCode > CURRENT_VERSION_CODE
    }

    suspend fun checkForUpdate(workerBaseUrl: String): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val api = ApiClient.getService(workerBaseUrl)
            val resp = api.checkAppVersion()
            if (!resp.isSuccessful) return@withContext null
            val body: AppVersionResponse = resp.body() ?: return@withContext null
            if (body.latest_version_code <= 0) return@withContext null
            UpdateInfo(
                versionCode = body.latest_version_code,
                versionName = body.latest_version_name ?: body.latest_version_code.toString(),
                changelogEn = body.changelog_en ?: "",
                changelogFa = body.changelog_fa ?: "",
                apkUrl = body.apk_url ?: "",
                exeUrl = body.exe_url ?: "",
                mandatory = body.mandatory
            )
        } catch (_: Exception) {
            null // offline: keep current version silently
        }
    }

    /**
     * Downloads the APK via DownloadManager and returns the download id.
     * When finished, [installDownloadedUpdate] is invoked by the receiver.
     */
    fun enqueueDownload(context: Context, apkUrl: String, versionName: String): Long {
        val request = DownloadManager.Request(Uri.parse(apkUrl)).apply {
            setTitle("NetOps Hub v${versionName}")
            setDescription("Downloading the new version update...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "netops-update-$versionName.apk")
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        return dm.enqueue(request)
    }

    fun getDownloadedFile(context: Context, downloadId: Long): File? {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        var cursor: Cursor? = null
        return try {
            cursor = dm.query(DownloadManager.Query().setFilterById(downloadId))
            if (cursor != null && cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    val uri = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                    uri?.let { File(Uri.parse(it).path!!) }
                } else null
            } else null
        } catch (_: Exception) {
            null
        } finally {
            cursor?.close()
        }
    }

    /** Triggers the Android package installer for the freshly downloaded APK. */
    fun installDownloadedUpdate(context: Context, apkFile: File) {
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apkFile)
        } else {
            Uri.fromFile(apkFile)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun registerDownloadReceiver(context: Context, onCompleted: (File?) -> Unit): BroadcastReceiver {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id != -1L) onCompleted(getDownloadedFile(context, id))
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        }
        return receiver
    }
}
