package xyz.siddharthseth.panettone

import android.Manifest
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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

    val TAG = "CameraFragment"
    private var flash = 0
    private var cameraFacing = 0
    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fotoApparatBack = setupCamera(LensPosition.BACK)
        fotoApparatFront = setupCamera(LensPosition.FRONT)

        fotoapparatSwitcher = setupCameraSwitcher(fotoApparatBack)

        cameraView.setOnLongClickListener {
            fotoapparatSwitcher.currentFotoapparat.focus()
            true
        }

        shutter.setOnClickListener { takePicture(fotoapparatSwitcher) }

        flashToggle.setOnClickListener { toggleFlash(fotoapparatSwitcher) }

        cameraFacingToggle.setOnClickListener {
            toggleCameraFacing(fotoapparatSwitcher, fotoApparatBack, fotoApparatFront)
        }
    }

    private fun setupCamera(lensPosition: LensPosition): Fotoapparat {
        val fotoApparat = Fotoapparat.with(context).into(cameraView).sensorSensitivity(
                SensorSensitivitySelectors.highestSensorSensitivity()).previewScaleType(
                ScaleType.CENTER_CROP).photoSize(SizeSelectors.biggestSize()).lensPosition(
                LensPositionSelectors.lensPosition(lensPosition)).focusMode(
                Selectors.firstAvailable(FocusModeSelectors.continuousFocus(),
                        FocusModeSelectors.autoFocus(), FocusModeSelectors.fixed())).flash(
                Selectors.firstAvailable(FlashSelectors.autoFlash(), FlashSelectors.off(),
                        FlashSelectors.on())).logger(Loggers.logcat()).build()
        return fotoApparat
    }

    private fun setupCameraSwitcher(fotoApparatBack: Fotoapparat): FotoapparatSwitcher {
        val fotoapparatSwitcher = FotoapparatSwitcher.withDefault(fotoApparatBack)
        return fotoapparatSwitcher
    }

    private fun toggleCameraFacing(fotoapparatSwitcher: FotoapparatSwitcher,
            fotoApparatBack: Fotoapparat, fotoApparatFront: Fotoapparat) {
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
    }

    private fun takePicture(fotoapparatSwitcher: FotoapparatSwitcher) {
        val result = fotoapparatSwitcher.currentFotoapparat.takePicture()

        val parentDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Panettone/")
        parentDir.mkdirs()
        val cacheFile = File(context.cacheDir, System.currentTimeMillis().toString() + ".bat")
        val finalFile = File(parentDir, System.currentTimeMillis().toString() + ".jpg")
        finalFile.createNewFile()

        val pendingResult = result?.saveToFile(cacheFile)
        pendingResult?.whenAvailable {
            Log.d(TAG, "image saved in cache " + cacheFile.name)
            val inputImage = Image.newInstance(context, Uri.fromFile(cacheFile))
            val outputImage = Image.newInstance(context, Uri.fromFile(finalFile))

            openUCrop(inputImage, outputImage)
        }
    }

    private fun toggleFlash(fotoapparatSwitcher: FotoapparatSwitcher) {
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
        Log.v(TAG, "openUCrop")

        val options = UCrop.Options()
        options.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
        options.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
        options.setLogoColor(ContextCompat.getColor(context, R.color.colorPrimary))
        options.setActiveWidgetColor(ContextCompat.getColor(context, R.color.colorPrimary))

        try {
            UCrop.of(inputImage.uri, outputImage.uri).withOptions(options).start(activity, 3)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        checkForPermissions()
        fotoapparatSwitcher.start()
    }

    private fun checkForPermissions() {
        val permission1 = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permission1 != PackageManager.PERMISSION_GRANTED) fragmentManager.popBackStack()
    }


    override fun onStop() {
        super.onStop()
        fotoapparatSwitcher.stop()
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
        lateinit var fotoapparatSwitcher: FotoapparatSwitcher
        lateinit var fotoApparatFront: Fotoapparat
        lateinit var fotoApparatBack: Fotoapparat

        fun newInstance(): CameraFragment {
            val fragment = CameraFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}
