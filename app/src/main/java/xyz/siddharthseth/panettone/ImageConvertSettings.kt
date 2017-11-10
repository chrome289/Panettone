package xyz.siddharthseth.panettone


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * A simple [Fragment] subclass.
 */
class ImageConvertSettings : Fragment() {

    lateinit var fview: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fview = inflater.inflate(R.layout.fragment_image_convert_settings, container, false)


        return fview
    }

}