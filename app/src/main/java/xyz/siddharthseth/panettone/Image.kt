package xyz.siddharthseth.panettone

import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import java.io.File

class Image {
    internal var fileName: String = ""
    internal var fileSize: Long = 0

    internal var height: Int = 0
    internal var width: Int = 0

    internal var uri: Uri = Uri.EMPTY
    internal var fileUrl: Uri = Uri.EMPTY

    companion object {

        val TAG = "ImageClass"

        //for mediastore files
        fun newInstance(context: Context, uri: Uri): Image {
            val image = Image()

            image.uri = uri

            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)


                image.fileName = cursor.getString(nameIndex)
                image.fileSize = cursor.getLong(sizeIndex)

                cursor.close()
            } else {
                image.fileName = File(uri.path).name
                image.fileSize = File(uri.path).length()
            }
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)

            image.width = options.outWidth
            image.height = options.outHeight

            Log.d(TAG, "image object created named " + image.fileName)
            Log.d(TAG, "the url is " + image.uri)

            return image
        }

        //for cache files
        fun newInstance(context: Context, fileName: String): Image {
            val image = Image()

            val parentDir = context.cacheDir

            parentDir.mkdirs()

            val finalFile = File(parentDir, fileName)
            if (finalFile.exists()) finalFile.delete()
            finalFile.createNewFile()

            image.uri = Uri.fromFile(finalFile)
            image.fileUrl = Uri.fromFile(finalFile)
            image.fileName = fileName

            Log.d(TAG, "image object created named " + image.fileName)
            Log.d(TAG, "the url is " + image.uri)
            return image
        }

        fun copyImageToStorage(context: Context, cacheUri: Uri): Uri {
            val image = Image()

            val parentDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Panettone/")

            parentDir.mkdirs()

            val cacheFile = File(cacheUri.path)

            val finalFile = File(parentDir, cacheFile.name)

            cacheFile.copyTo(finalFile, true, DEFAULT_BUFFER_SIZE)
            cacheFile.delete()

            image.uri = Uri.fromFile(finalFile)
            image.fileName = finalFile.name

            Log.d(TAG, "image object created named " + image.fileName)
            Log.d(TAG, "the url is " + image.uri)

            return image.uri
        }
    }

    fun getExtension(): String {
        val temp = this.fileName.split(".")
        return temp[temp.size - 1]
    }

    fun getFileSize(): String {
        if (this.fileSize < 1024) return (this.fileSize).toString() + " B"
        else if (this.fileSize < (1024 * 1024)) return (this.fileSize / 1024).toString() + " KB"
        else return (this.fileSize / (1024 * 1024)).toString() + "." + String.format("%.0f",
                (this.fileSize % (1024 * 1024)) / (1024 * 10.24f)) + " MB"
    }
}