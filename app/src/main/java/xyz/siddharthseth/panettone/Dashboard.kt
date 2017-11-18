package xyz.siddharthseth.panettone

import android.Manifest
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dashboard.*

class Dashboard : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentManager.popBackStack()

        gallerySelector.setOnClickListener {
            mListener?.openImageResize()
        }

        cameraSelector.setOnClickListener {
            val bool = checkForPermissions()
            if (bool) mListener?.openCameraFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun checkForPermissions(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        val permission2 = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        val array = ArrayList<String>()

        if (permission1 != PackageManager.PERMISSION_GRANTED) array.add(Manifest.permission.CAMERA)
        if (permission2 != PackageManager.PERMISSION_GRANTED) array.add(
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (array.size == 0) return true
        else {
            askForPermission(array.toTypedArray())
            return false
        }
    }

    private fun askForPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(activity, permissions, 4)
    }


    interface OnFragmentInteractionListener {
        fun openCameraFragment()
        fun openImageResize()
    }

    companion object {
        fun newInstance(): Dashboard {
            val fragment = Dashboard()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
