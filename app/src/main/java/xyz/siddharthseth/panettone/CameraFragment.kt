package xyz.siddharthseth.panettone


import android.app.Fragment
import android.content.Context
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
    var fotoApparat: Fotoapparat? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fotoApparat = Fotoapparat.with(context).into(cameraView).previewScaleType(
                ScaleType.CENTER_CROP).photoSize(SizeSelectors.biggestSize()).lensPosition(
                LensPositionSelectors.back()).focusMode(
                Selectors.firstAvailable(FocusModeSelectors.continuousFocus(),
                        FocusModeSelectors.autoFocus(), FocusModeSelectors.fixed())).flash(
                Selectors.firstAvailable(FlashSelectors.autoFlash(), FlashSelectors.off(),
                        FlashSelectors.on())).build()


        shutter.setOnClickListener {
            if (fotoApparat != null) {
                val result = fotoApparat!!.takePicture()

                val parentDir = File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "Panettone/")
                parentDir.mkdirs()
                val cacheFile = File(context.cacheDir,
                        System.currentTimeMillis().toString() + "-bat")
                val finalFile = File(parentDir, System.currentTimeMillis().toString() + ".jpg")
                finalFile.createNewFile()

                val pendingResult = result.saveToFile(cacheFile)
                pendingResult.whenAvailable {
                    Log.d(TAG, "image saved in cache")
                    val inputImage = Image.newInstance(context, Uri.fromFile(cacheFile))
                    val outputImage = Image.newInstance(context, Uri.fromFile(finalFile))

                    openUCrop(inputImage, outputImage)
                }
            }
        }

        flashToggle.setOnClickListener {
            when (Integer.parseInt(flashToggle.tag.toString())) {
                0 -> {
                    flashToggle.setImageResource(R.drawable.ic_flash_off)
                    flashToggle.tag = "1"
                    flash = 1
                    fotoApparat?.updateParameters(
                            UpdateRequest.builder().flash(FlashSelectors.off()).build())
                }
                1 -> {
                    flashToggle.setImageResource(R.drawable.ic_flash_on)
                    flashToggle.tag = "2"
                    flash = 2
                    fotoApparat?.updateParameters(
                            UpdateRequest.builder().flash(FlashSelectors.on()).build())
                }
                2 -> {
                    flashToggle.setImageResource(R.drawable.ic_highlight)
                    flashToggle.tag = "3"
                    flash = 3
                    fotoApparat?.updateParameters(
                            UpdateRequest.builder().flash(FlashSelectors.torch()).build())
                }
                3 -> {
                    flashToggle.setImageResource(R.drawable.ic_flash_auto)
                    flashToggle.tag = "0"
                    flash = 0
                    fotoApparat?.updateParameters(
                            UpdateRequest.builder().flash(FlashSelectors.autoFlash()).build())
                }
            }
        }

        cameraFacingToggle.setOnClickListener {
            if (cameraFacingToggle.tag.toString() == "0") {
                cameraFacingToggle.setImageResource(R.drawable.ic_camera_front)
                cameraFacingToggle.tag = "1"
                cameraFacing = 1
                fotoApparat?.stop()
                fotoApparat = Fotoapparat.with(context).into(cameraView).previewScaleType(
                        ScaleType.CENTER_CROP).photoSize(SizeSelectors.biggestSize()).lensPosition(
                        LensPositionSelectors.front()).focusMode(
                        Selectors.firstAvailable(FocusModeSelectors.continuousFocus(),
                                FocusModeSelectors.autoFocus(), FocusModeSelectors.fixed())).flash(
                        Selectors.firstAvailable(FlashSelectors.autoFlash(), FlashSelectors.off(),
                                FlashSelectors.on())).build()
                fotoApparat?.start()
            } else {
                cameraFacingToggle.setImageResource(R.drawable.ic_camera_rear)
                cameraFacingToggle.tag = "0"
                cameraFacing = 0
                fotoApparat?.stop()
                fotoApparat = Fotoapparat.with(context).into(cameraView).previewScaleType(
                        ScaleType.CENTER_CROP).photoSize(SizeSelectors.biggestSize()).lensPosition(
                        LensPositionSelectors.back()).focusMode(
                        Selectors.firstAvailable(FocusModeSelectors.continuousFocus(),
                                FocusModeSelectors.autoFocus(), FocusModeSelectors.fixed())).flash(
                        Selectors.firstAvailable(FlashSelectors.autoFlash(), FlashSelectors.off(),
                                FlashSelectors.on())).build()
                fotoApparat?.start()
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
        fotoApparat?.start()
    }

    override fun onStop() {
        super.onStop()
        fotoApparat?.stop()
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
