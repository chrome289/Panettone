package xyz.siddharthseth.panettone

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_image_share.*

class ImageShare : Fragment() {
    var image: Image = Image()

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView")

        return inflater.inflate(R.layout.fragment_image_share, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")

        //toolbar customization
        toolbar.title = "Image Converted"

        activity.setActionBar(toolbar)
        activity.actionBar.setDisplayShowHomeEnabled(true)
        activity.actionBar.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            fragmentManager.popBackStack()
        }

        //intent for sharing final image
        share.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, image.uri)
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }

        //using glide to load image in ui
        GlideApp.with(this).load(image.uri).diskCacheStrategy(DiskCacheStrategy.NONE).into(img_save)

        resultFileName.text = image.fileName

        //file info
        val temp = image.getFileSize() + ", " + image.width + " x " + image.height + ", " + image.fileExtension.toUpperCase()
        info.text = temp
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach")

        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(
                    context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach")

        mListener = null
    }

    interface OnFragmentInteractionListener

    companion object {
        val TAG = "ImageShare"
        fun newInstance(shareImage: Image): ImageShare {
            val fragment = ImageShare()
            fragment.image = shareImage

            return fragment
        }
    }
}