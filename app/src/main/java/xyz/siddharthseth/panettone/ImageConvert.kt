package xyz.siddharthseth.panettone


import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_image_convert.*


class ImageConvert : Fragment(), View.OnClickListener {
    override fun onClick(p0: View?) {
        fragmentManager.popBackStack()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_convert, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity.setActionBar(toolbar)
        activity.actionBar.setDisplayHomeAsUpEnabled(true)
        activity.actionBar.setDisplayShowHomeEnabled(true)

        toolbar.setNavigationOnClickListener(this)

    }


}
