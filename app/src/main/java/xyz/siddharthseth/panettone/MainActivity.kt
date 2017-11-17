package xyz.siddharthseth.panettone

import android.app.FragmentManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.yalantis.ucrop.UCrop





class MainActivity : AppCompatActivity()
        , Dashboard.OnFragmentInteractionListener, CameraFragment.OnFragmentInteractionListener,
        GalleryImageSelect.OnFragmentInteractionListener,
        ImageShare.OnFragmentInteractionListener {

    val TAG = "MainActivity"
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
            if (requestCode == 2) {
                if (resultCode != UCrop.RESULT_ERROR && requestCode != UCrop.RESULT_ERROR) {
                    val resultUri: Uri? = UCrop.getOutput(data)
                    if (resultUri != null) {
                        Log.d(TAG, resultUri.path + " --- path")
                        val image = Image.copyImageToStorage(this, resultUri)
                        openImageShare(image)
                    }
                } else if (resultCode == UCrop.RESULT_ERROR) {
                    val cropError = UCrop.getError(data)
                    Log.v(TAG, "error for cropping " + cropError)
                } else {
                    fragmentManager.popBackStack()
                }
            }
        } else {
            Log.e(TAG, "onActivityResult null for requestcode " + resultCode)
        }
    }
}