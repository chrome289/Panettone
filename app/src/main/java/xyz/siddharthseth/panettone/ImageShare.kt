package xyz.siddharthseth.panettone

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_image_share.*

class ImageShare : Fragment(), View.OnClickListener {

    private var croppedUri: Uri = Uri.EMPTY

    private var mListener: OnFragmentInteractionListener? = null

    override fun onClick(p0: View?) {
        fragmentManager.popBackStack()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_share, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        croppedUri = Uri.parse(arguments.getString("croppedUri"))
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        share.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, croppedUri)
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(Intent.createChooser(shareIntent, "Share"))
        }

        GlideApp.with(this).load(croppedUri).diskCacheStrategy(DiskCacheStrategy.NONE).into(
                img_save)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is OnFragmentInteractionListener) {
            context
        } else {
            throw RuntimeException(
                    context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface OnFragmentInteractionListener

    companion object {
        fun newInstance(croppedUri: Uri): ImageShare {
            val fragment = ImageShare()
            val args = Bundle()
            args.putString("croppedUri", croppedUri.toString())
            fragment.arguments = args
            return fragment
        }
    }
}