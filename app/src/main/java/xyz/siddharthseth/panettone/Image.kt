package xyz.siddharthseth.panettone

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.io.File

class Image {
    var fileName: String = ""
    var fileSize: Long = 0

    var height: Int = 0
    var width: Int = 0

    var uri: Uri = Uri.EMPTY

    companion object {

        val TAG = "ImageClass"

        //for mediastore files
        fun newInstance(context: Context, uri: Uri): Image {
            val image = Image()

            image.uri = uri

            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)

                cursor.moveToFirst()

                image.fileName = cursor.getString(nameIndex)
                image.fileSize = cursor.getLong(sizeIndex)

                cursor.close()

                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null,
                        options)

                image.width = options.outWidth
                image.height = options.outHeight
            }

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
            image.fileName = fileName

            Log.d(TAG, "image object created named " + image.fileName)
            Log.d(TAG, "the url is " + image.uri)
            return image
        }

        fun copyImageToStorage(context: Context, cacheUri: Uri): Image {
            val image = Image()

            val parentDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Panettone/")

            parentDir.mkdirs()

            val cacheFile = File(cacheUri.path)

            val finalFile = File(parentDir, cacheFile.name)
            if (finalFile.exists()) finalFile.delete()

            cacheFile.copyTo(finalFile, true, DEFAULT_BUFFER_SIZE)
            cacheFile.delete()

            image.uri = getContentProviderUri(context, Uri.fromFile(finalFile))
            image.fileName = finalFile.name

            Log.d(TAG, "image object created named " + image.fileName)
            Log.d(TAG, "the url is " + image.uri)

            return image
        }

        private fun getContentProviderUri(context: Context, resultUri: Uri): Uri {
            val cursor = context.contentResolver.query(resultUri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
                cursor.close()
                return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id)
            } else {
                Log.d(TAG, "result uri in get contentprovider fun " + resultUri.toString())
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, resultUri.path)
                return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
            }
        }
    }
}