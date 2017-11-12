package xyz.siddharthseth.panettone

import android.app.FragmentManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity


class MainActivity : AppCompatActivity()
        , Dashboard.OnFragmentInteractionListener
        , CameraFragment.OnFragmentInteractionListener
        , ImageResize.OnFragmentInteractionListener {


    override fun openImageResize(inputUri: String, outputUri: String, imageWidth: Int, imageHeight: Int) {
        fragmentManager.beginTransaction()
                .add(R.id.frame, ImageResize.newInstance(inputUri
                        , outputUri
                        , imageWidth
                        , imageHeight))
                .addToBackStack("imageResizer")
                .commit()
    }

    override fun openCameraFragment() {
        fragmentManager.beginTransaction()
                .add(R.id.frame, CameraFragment.newInstance())
                .addToBackStack("camera")
                .commit()
    }

    fun openDashboard() {
        val fragmentManager: FragmentManager = fragmentManager
        fragmentManager.beginTransaction()
                .replace(R.id.frame, Dashboard.newInstance())
                .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openDashboard()

    }


}
