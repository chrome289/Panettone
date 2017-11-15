package xyz.siddharthseth.panettone

import android.app.FragmentManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity


class MainActivity : AppCompatActivity()
        , Dashboard.OnFragmentInteractionListener
        , CameraFragment.OnFragmentInteractionListener, ImageResize.OnFragmentInteractionListener,
        ImageShare.OnFragmentInteractionListener {

    override fun openImageSave(croppedUri: Uri) {
        fragmentManager.beginTransaction().add(R.id.frame,
                ImageShare.newInstance(croppedUri)).addToBackStack("imageSave").commit()
    }

    override fun openImageResize(inputUri: String, outputUri: String, imageWidth: Int,
            imageHeight: Int) {
        fragmentManager.beginTransaction().add(R.id.frame,
                ImageResize.newInstance(inputUri, outputUri, imageWidth,
                        imageHeight)).addToBackStack("imageResizer").commit()
    }

    override fun openCameraFragment() {
        fragmentManager.beginTransaction().add(R.id.frame,
                CameraFragment.newInstance()).addToBackStack("camera").commit()
    }

    private fun openDashboard() {
        val fragmentManager: FragmentManager = fragmentManager
        fragmentManager.beginTransaction().replace(R.id.frame, Dashboard.newInstance()).commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openDashboard()
    }
}