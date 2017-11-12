package xyz.siddharthseth.panettone

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dashboard.*


class Dashboard : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_dashboard, container, false)

    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentManager.popBackStack()

        gallerySelector.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, 1)
        }

        cameraSelector.setOnClickListener {
            mListener?.openCameraFragment()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && data != null) {

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(activity.contentResolver.openInputStream(data.data), null, options)
            mListener?.openImageResize(data.data.toString()
                    , activity.filesDir.toString() + "/1.jpg"
                    , options.outWidth
                    , options.outHeight)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    interface OnFragmentInteractionListener {
        fun openCameraFragment()
        fun openImageResize(inputUri: String, outputUri: String, imageWidth: Int, imageHeight: Int)
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
