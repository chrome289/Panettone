package xyz.siddharthseth.panettone

import android.app.Fragment
import android.content.Context
import android.os.Bundle
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
            mListener?.openCameraFragment()
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
