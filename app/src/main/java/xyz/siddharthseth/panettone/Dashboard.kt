package xyz.siddharthseth.panettone


import android.os.Bundle
import android.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class Dashboard :Fragment() {

    lateinit var fview: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fview =inflater.inflate(R.layout.fragment_dashboard, container, false)

        val buttonCam: Button = fview.findViewById(R.id.cameraSelector)
        val buttonGal:Button= fview.findViewById(R.id.gallerySelector)

        buttonGal.setOnClickListener {

        }

        return fview
    }
}
