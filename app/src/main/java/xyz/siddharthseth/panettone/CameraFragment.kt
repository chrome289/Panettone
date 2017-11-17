package xyz.siddharthseth.panettone


import android.app.Fragment
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wonderkiln.camerakit.*
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class CameraFragment : Fragment(), CameraKitEventListener {

    val TAG = "CameraFragment"
    private var flash = 0
    private var cameraFacing = 0
    private var mListener: OnFragmentInteractionListener? = null

    override fun onVideo(p0: CameraKitVideo?) {
        Log.v(TAG, "video event")
    }

    override fun onEvent(p0: CameraKitEvent?) {
        Log.v(TAG, "generic event")
    }

    override fun onImage(p0: CameraKitImage?) {
        Log.v(TAG, "image event")
        if (p0 != null) {
            val byteArray = p0.jpeg
            Log.v(TAG, byteArray.size.toString())

            val file = File(context.cacheDir, System.currentTimeMillis().toString() + ".jpg")
            val file2 = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    "Panettone/")
            file2.mkdirs()

            val file3 = File(file2, System.currentTimeMillis().toString() + ".jpg")
            val bufferedOutputStream = BufferedOutputStream(FileOutputStream(file))
            bufferedOutputStream.write(byteArray)
            bufferedOutputStream.flush()
            bufferedOutputStream.close()
            file.copyTo(file3, true, DEFAULT_BUFFER_SIZE)

            UCrop.of(Uri.fromFile(file), Uri.fromFile(file3)).start(activity, 3)
        }
    }

    override fun onError(p0: CameraKitError?) {
        Log.v(TAG, "error event")
        if (p0 != null) Log.v(TAG, p0.message)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraView.bindCameraKitListener(this)
        cameraView.addCameraKitListener(this)

        shutter.setOnClickListener {
            cameraView.captureImage()
        }

        flashToggle.setOnClickListener {
            when (Integer.parseInt(flashToggle.tag.toString())) {
                0 -> {
                    flashToggle.setImageResource(R.drawable.ic_flash_off)
                    flashToggle.tag = "1"
                    flash = 1
                    cameraView.flash = CameraKit.Constants.FLASH_OFF
                }
                1 -> {
                    flashToggle.setImageResource(R.drawable.ic_flash_on)
                    flashToggle.tag = "2"
                    flash = 2
                    cameraView.flash = CameraKit.Constants.FLASH_ON
                }
                2 -> {
                    flashToggle.setImageResource(R.drawable.ic_highlight)
                    flashToggle.tag = "3"
                    flash = 3
                    cameraView.flash = CameraKit.Constants.FLASH_TORCH
                }
                3 -> {
                    flashToggle.setImageResource(R.drawable.ic_flash_auto)
                    flashToggle.tag = "0"
                    flash = 0
                    cameraView.flash = CameraKit.Constants.FLASH_AUTO
                }
            }
        }

        cameraFacingToggle.setOnClickListener {
            if (cameraFacingToggle.tag.toString() == "0") {
                cameraFacingToggle.setImageResource(R.drawable.ic_camera_front)
                cameraFacingToggle.tag = "1"
                cameraFacing = 1
                cameraView.facing = CameraKit.Constants.FACING_FRONT
            } else {
                cameraFacingToggle.setImageResource(R.drawable.ic_camera_rear)
                cameraFacingToggle.tag = "0"
                cameraFacing = 0
                cameraView.facing = CameraKit.Constants.FACING_BACK
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cameraView.start()
    }

    override fun onStop() {
        super.onStop()
        cameraView.stop()
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


    interface OnFragmentInteractionListener

    companion object {
        fun newInstance(): CameraFragment {
            val fragment = CameraFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
