package xyz.siddharthseth.panettone

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap.CompressFormat.PNG
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yalantis.ucrop.UCrop
import java.io.File

class ImageResize : Fragment() {

    private var inputUri: Uri = Uri.EMPTY
    private var outputUri: Uri = Uri.EMPTY
    private var imageHeight = 0
    private var imageWidth = 0

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inputUri = Uri.parse(arguments.getString("inputUri"))
        outputUri = Uri.parse(arguments.getString("outputUri"))
        imageHeight = arguments.getInt("imageHeight")
        imageWidth = arguments.getInt("imageWidth")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_resizer, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val temp = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Panettone/")

        Log.v("nero", "first " + temp.mkdirs())
        val finalFile = File(temp, "1.jpg")
        if (finalFile.exists()) finalFile.delete()
        finalFile.createNewFile()

        val options = UCrop.Options()
        options.setCompressionFormat(PNG)

        outputUri = Uri.fromFile(finalFile)
        UCrop.of(inputUri, outputUri).withOptions(options)
                .start(context, this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        Log.v("nero", "nero")
        if (resultCode != UCrop.RESULT_ERROR && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data)
            if (resultUri != null) mListener?.openImageSave(resultUri)
        } else if (resultCode != UCrop.RESULT_ERROR && requestCode != UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data)
            if (resultUri != null) mListener?.openImageSave(resultUri)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data)
            Log.v("nero", "error for cropping " + cropError)
        } else {
            fragmentManager.popBackStack()
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
        fun openImageSave(croppedUri: Uri)
    }

    companion object {
        fun newInstance(inputUri: String, outputUri: String, imageWidth: Int, imageHeight: Int): ImageResize {
            val fragment = ImageResize()
            val args = Bundle()
            args.putString("inputUri", inputUri)
            args.putString("outputUri", outputUri)
            args.putInt("imageWidth", imageWidth)
            args.putInt("imageHeight", imageHeight)
            fragment.arguments = args
            return fragment
        }
    }
}
