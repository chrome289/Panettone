package xyz.siddharthseth.panettone.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import xyz.siddharthseth.panettone.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        askForPermissions()
    }

    /**
     * ask for permission, finish if not given
     */
    private fun askForPermissions() {
        //permission
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        var permissionList = Array(0) { "" }

        if (permission == PackageManager.PERMISSION_DENIED) {
            permissionList = permissionList.plus(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList, REQUEST_CODE_PERMISSION)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                //if any permission is not granted don't open the camera
                val bool = grantResults.any { it == PackageManager.PERMISSION_DENIED }
                if (bool) {
                    finish()
                } else {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE_PERMISSION = 1
    }
}
