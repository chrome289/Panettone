package xyz.siddharthseth.panettone

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_image_share.*

class ImageShare : Fragment(), View.OnClickListener, Toolbar.OnMenuItemClickListener {
    override fun onMenuItemClick(p0: MenuItem?): Boolean {
        Log.v(TAG, "tag")
        return false
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            Log.v(TAG, "tag")
        }
        Log.v(TAG, "tag")
        return super.onOptionsItemSelected(menuItem)
    }

    var image: Image = Image()

    private var mListener: OnFragmentInteractionListener? = null

    private val TAG = "ImageShare"

    override fun onClick(p0: View?) {
        Log.v(TAG, "tag")
        fragmentManager.popBackStack()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView")

        return inflater.inflate(R.layout.fragment_image_share, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated")

        toolbar.title = "Image Converted"

        activity.setActionBar(toolbar)
        activity.actionBar.setDisplayShowHomeEnabled(true)
        activity.actionBar.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            fragmentManager.popBackStack()
        }


        share.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, image.uri)
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }

        GlideApp.with(this).load(image.uri).diskCacheStrategy(DiskCacheStrategy.NONE).into(img_save)

        resultFileName.text = image.fileName

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
        fun newInstance(shareImage: Image): ImageShare {
            val fragment = ImageShare()

            fragment.image = shareImage

            return fragment
        }
    }
}