package xyz.siddharthseth.panettone

import android.app.FragmentManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.yalantis.ucrop.UCrop

class MainActivity : AppCompatActivity()
        , Dashboard.OnFragmentInteractionListener, CameraFragment.OnFragmentInteractionListener,
        GalleryImageSelect.OnFragmentInteractionListener,
        ImageShare.OnFragmentInteractionListener {

    private val TAG = "MainActivity"
    override fun openImageShare(shareImage: Image) {
        Log.v(TAG, "open image share")
        fragmentManager.beginTransaction().add(R.id.frame,
                ImageShare.newInstance(shareImage)).addToBackStack("imageSave").commit()
    }

    override fun openImageResize() {
        Log.v(TAG, "open image resize")
        fragmentManager.beginTransaction().add(R.id.frame,
                GalleryImageSelect.newInstance()).addToBackStack("imageResizer").commit()
    }

    override fun openCameraFragment() {
        Log.v(TAG, "open camera fragment")
        fragmentManager.beginTransaction().add(R.id.frame,
                CameraFragment.newInstance()).addToBackStack("camera").commit()
    }

    private fun openDashboard() {
        Log.v(TAG, "open dashboard")
        val fragmentManager: FragmentManager = fragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame, Dashboard.newInstance()).commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "on create")
        setContentView(R.layout.activity_main)
        openDashboard()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (requestCode == 2 || requestCode == 3) {
                if (resultCode != UCrop.RESULT_ERROR && requestCode != UCrop.RESULT_ERROR) {
                    val resultUri: Uri = UCrop.getOutput(data)!!
                    val compressFormat = when (data.extras.getInt(UCrop.EXTRA_OUTPUT_FORMAT)) {
                        0 -> "jpg"
                        1 -> "png"
                        2 -> "webp"
                        else -> "jpg"
                    }
                    val uri: Uri = Image.copyImageToStorage(resultUri, compressFormat)
                    openImageShare(Image.newInstance(this, uri))
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    val cropError = UCrop.getError(data)
                    cropError?.printStackTrace()
                } else {
                    fragmentManager.popBackStack()
                }
            }
        } else {
            Log.e(TAG, "onActivityResult null for requestcode " + requestCode)
        }
        cacheDir.deleteRecursively()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            4 -> {
                val bool = grantResults.none { it != PackageManager.PERMISSION_GRANTED }

                if (bool) openCameraFragment()
            }
        }
    }
}