package xyz.siddharthseth.panettone

import android.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = getFragmentManager()
        fragmentManager.beginTransaction()
                .replace(R.id.frame, Dashboard())
                .commit()

    }
}
