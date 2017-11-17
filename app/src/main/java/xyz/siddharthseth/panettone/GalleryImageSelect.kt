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

    val TAG = "GalleryImageSelect"

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_gallery_image_select, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.v(TAG, "onViewCreated")
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.v(TAG, "onActivityResult for request code " + requestCode)
        if (requestCode == 1) {

            val inputImage = Image.newInstance(context, Uri.parse(data.data.toString()))
            val outputImage = Image.newInstance(context, inputImage.fileName)

            openUCrop(inputImage, outputImage)
        }
    }

    private fun openUCrop(inputImage: Image, outputImage: Image) {
        Log.v(TAG, "openUCrop")

        val options = UCrop.Options()
        options.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        options.setLogoColor(ContextCompat.getColor(context, R.color.colorPrimary))
        options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorPrimary))

        try {
            UCrop.of(inputImage.uri, outputImage.uri).withOptions(options).start(activity, 2)
        } catch (exception: Exception) {
            exception.printStackTrace()
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

    interface OnFragmentInteractionListener {
        fun openImageShare(shareImage: Image)
    }

    companion object {
        fun newInstance(): GalleryImageSelect {
            val fragment = GalleryImageSelect()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
