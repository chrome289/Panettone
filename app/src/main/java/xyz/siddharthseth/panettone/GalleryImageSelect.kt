package xyz.siddharthseth.panettone

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yalantis.ucrop.UCrop

class GalleryImageSelect : Fragment() {

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_gallery_image_select, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        //gallery intent reciever
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult for request code " + requestCode)
        if (requestCode == 1) {
            //get image object for selected image
            val inputImage = Image.newInstance(activity, Uri.parse(data.data.toString()))
            val outputImage = Image.newInstance(activity, inputImage.getFileNameWoExt())

            openUCrop(inputImage, outputImage)
        }
    }

    private fun openUCrop(inputImage: Image, outputImage: Image) {
        Log.d(TAG, "openUCrop")

        //ucrop ui setup
        val options = UCrop.Options()
        options.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
        options.setLogoColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        options.setActiveWidgetColor(ContextCompat.getColor(activity, R.color.colorPrimary))

        try {
            UCrop.of(inputImage.uri, outputImage.uri).withOptions(options).start(activity, 2)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")

        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")
        mListener = null
    }

    interface OnFragmentInteractionListener {
        fun openImageShare(shareImage: Image)
    }

    companion object {

        private val TAG = "GalleryImageSelect"

        fun newInstance(): GalleryImageSelect {
            Log.d(TAG, "newInstance")
            val fragment = GalleryImageSelect()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
