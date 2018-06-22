package xyz.siddharthseth.panettone.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.support.v4.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    fun createImageFile(): Uri {
        // Create an image file name
        val context: Context = getApplication()
        val parentDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        parentDir.mkdirs()

        return Uri.fromFile(File(parentDir, "IMG_${getTimestamp()}"))
    }

    fun createCameraImageFile(fileProvider: String) {
        // Create an image file name
        val context: Context = getApplication()
        val parentDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        parentDir.mkdirs()

        val image = File(parentDir, "IMG_${getTimestamp()}")

        // Save a file: path for use with ACTION_VIEW  intents
        cameraImageUri = FileProvider.getUriForFile(context, fileProvider, image)
    }

    private fun getTimestamp(): String {
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US)
        simpleDateFormat.timeZone = calendar.timeZone
        return simpleDateFormat.format(calendar.time)
    }


    internal fun copyCacheImage(cacheUri: Uri, compressFormat: String): String {
        //copy cache file to storage
        val parentDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Panettone/")

        parentDir.mkdirs()

        val cacheFile = File(cacheUri.path)

        val finalFile = File(parentDir, cacheFile.nameWithoutExtension + "." + compressFormat)
        finalFile.createNewFile()

        cacheFile.copyTo(finalFile, true, DEFAULT_BUFFER_SIZE)
        clearCacheFiles()

        return finalFile.path

    }

    private fun clearCacheFiles() {
        val context: Context = getApplication()
        val parentDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        for (file in parentDir.listFiles())
            file.delete()
    }

    internal var cameraImageUri: Uri = Uri.EMPTY

}