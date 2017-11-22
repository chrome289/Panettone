package xyz.siddharthseth.panettone

import android.Manifest
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yalantis.ucrop.UCrop
import io.fotoapparat.Fotoapparat
import io.fotoapparat.FotoapparatSwitcher
import io.fotoapparat.log.Loggers
import io.fotoapparat.parameter.LensPosition
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.parameter.selector.*
import io.fotoapparat.parameter.update.UpdateRequest
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File

class CameraFragment : Fragment() {

    private var flash = 0
    private var cameraFacing = 0
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        //fotoapparat instance for back & front camera
        fotoApparatBack = setupCamera(LensPosition.BACK)
        fotoApparatFront = setupCamera(LensPosition.FRONT)

        //the switcher
        fotoapparatSwitcher = setupCameraSwitcher(fotoApparatBack)

        cameraView.setOnLongClickListener {
            fotoapparatSwitcher.currentFotoapparat.focus()
            true
        }

        //shutter event
        shutter.setOnClickListener { takePicture(fotoapparatSwitcher) }

        //flash toggle event
        flashToggle.setOnClickListener { toggleFlash(fotoapparatSwitcher) }

        //camera switcher
        cameraFacingToggle.setOnClickListener {
            toggleCameraFacing(fotoapparatSwitcher, fotoApparatBack, fotoApparatFront)
        }

        //custom zoomview for implementing scaling in cameraview
        zoomView.CustomZoomView(activity)
    }

    private fun setupCamera(lensPosition: LensPosition): Fotoapparat {
        Log.d(TAG, "setupCamera")

        //setup camera using the front or back position
        return Fotoapparat.with(activity).into(cameraView).sensorSensitivity(
                SensorSensitivitySelectors.highestSensorSensitivity()).previewScaleType(
                ScaleType.CENTER_CROP).photoSize(SizeSelectors.biggestSize()).lensPosition(
                LensPositionSelectors.lensPosition(lensPosition)).focusMode(
                Selectors.firstAvailable(FocusModeSelectors.continuousFocus(),
                        FocusModeSelectors.autoFocus(), FocusModeSelectors.fixed())).flash(
                Selectors.firstAvailable(FlashSelectors.autoFlash(), FlashSelectors.off(),
                        FlashSelectors.on())).logger(Loggers.logcat()).build()
    }

    private fun setupCameraSwitcher(fotoApparatBack: Fotoapparat): FotoapparatSwitcher {
        Log.d(TAG, "setupCameraSwitcher")
        //initialize fotoapparatswitcher
        return FotoapparatSwitcher.withDefault(fotoApparatBack)
    }

    private fun toggleCameraFacing(fotoapparatSwitcher: FotoapparatSwitcher,
            fotoApparatBack: Fotoapparat, fotoApparatFront: Fotoapparat) {
        Log.d(TAG, "toggleCameraFacing")

        //switch camera facing by switching tag value of the toggle
        //0 for BACK, 1 for FRONT
        if (cameraFacingToggle.tag.toString() == "0") {
            cameraFacingToggle.setImageResource(R.drawable.ic_camera_front)
            cameraFacingToggle.tag = "1"
            cameraFacing = 1

            fotoApparatFront.let { fotoapparatSwitcher.switchTo(it) }
        } else {
            cameraFacingToggle.setImageResource(R.drawable.ic_camera_rear)
            cameraFacingToggle.tag = "0"
            cameraFacing = 0

            fotoApparatBack.let { fotoapparatSwitcher.switchTo(it) }
        }

        //reset zoom on cameraview on switching camera
        zoomView.resetZoomFactor()
    }

    private fun takePicture(fotoapparatSwitcher: FotoapparatSwitcher) {
        Log.d(TAG, "takePicture")

        //getting the result intent for the image
        val result = fotoapparatSwitcher.currentFotoapparat.takePicture()

        val cacheFile = File(activity.cacheDir, System.currentTimeMillis().toString() + ".bat")
        cacheFile.createNewFile()


        val pendingResult = result?.saveToFile(cacheFile)
        pendingResult?.whenAvailable {
            Log.d(TAG, "image saved in cache " + cacheFile.name)
            //using the pendingintent saving image async to cache

            val inputImage = Image.newInstance(activity, Uri.fromFile(cacheFile))
            val outputImage = Image.newInstance(activity, inputImage.getFileNameWoExt())

            //open ucrop for editing
            openUCrop(inputImage, outputImage)
        }
    }

    private fun toggleFlash(fotoapparatSwitcher: FotoapparatSwitcher) {
        Log.d(TAG, "toggleFlash")

        //flash toggle
        //0 for AUTO, 1 for OFF, 2 for ON, 3 for TORCH
        when (Integer.parseInt(flashToggle.tag.toString())) {
            0 -> {
                flashToggle.setImageResource(R.drawable.ic_flash_off)
                flashToggle.tag = "1"
                flash = 1
                fotoapparatSwitcher.currentFotoapparat.updateParameters(
                        UpdateRequest.builder().flash(FlashSelectors.off()).build())
            }
            1 -> {
                flashToggle.setImageResource(R.drawable.ic_flash_on)
                flashToggle.tag = "2"
                flash = 2
                fotoapparatSwitcher.currentFotoapparat.updateParameters(
                        UpdateRequest.builder().flash(FlashSelectors.on()).build())
            }
            2 -> {
                flashToggle.setImageResource(R.drawable.ic_highlight)
                flashToggle.tag = "3"
                flash = 3
                fotoapparatSwitcher.currentFotoapparat.updateParameters(
                        UpdateRequest.builder().flash(FlashSelectors.torch()).build())
            }
            3 -> {
                flashToggle.setImageResource(R.drawable.ic_flash_auto)
                flashToggle.tag = "0"
                flash = 0
                fotoapparatSwitcher.currentFotoapparat.updateParameters(
                        UpdateRequest.builder().flash(FlashSelectors.autoFlash()).build())
            }
        }
    }

    private fun openUCrop(inputImage: Image, outputImage: Image) {
        Log.d(TAG, "openUCrop")

        //ucrop ui colors
        val options = UCrop.Options()
        options.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
        options.setLogoColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        options.setActiveWidgetColor(ContextCompat.getColor(activity, R.color.colorPrimary))

        try {
            UCrop.of(inputImage.uri, outputImage.uri).withOptions(options).start(activity, 3)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d(TAG, "onResume")

        checkForPermissions()
        fotoapparatSwitcher.start()
    }

    private fun checkForPermissions() {
        Log.d(TAG, "checkForPermission")

        //check for camera permission on starting camera
        val permission1 = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
        if (permission1 != PackageManager.PERMISSION_GRANTED) fragmentManager.popBackStack()
    }


    override fun onStop() {
        super.onStop()

        Log.d(TAG, "onStop")

        fotoapparatSwitcher.stop()
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
        lateinit var fotoapparatSwitcher: FotoapparatSwitcher
        lateinit var fotoApparatFront: Fotoapparat
        lateinit var fotoApparatBack: Fotoapparat

        private val TAG = "CameraFragment"

        fun newInstance(): CameraFragment {
            Log.d(TAG, "newInstance")

            val fragment = CameraFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }

        fun setZoomLevel(zoomLevel: Float) {
            Log.d(TAG, "setZoomLevel")

            //set zoom level for camera switcher
            fotoapparatSwitcher.currentFotoapparat.setZoom(zoomLevel)
        }
    }
}
