package xyz.siddharthseth.panettone.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.method.LinkMovementMethod
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*
import xyz.siddharthseth.panettone.R
import xyz.siddharthseth.panettone.viewmodel.MainActivityViewModel
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainActivityViewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        textView.movementMethod = LinkMovementMethod.getInstance()

        galleryButton.setOnClickListener {
            openGalleryImageSelection()
        }

        cameraButton.setOnClickListener {
            openCamera()
        }

        if (intent.type != null) {
            if (intent.action == Intent.ACTION_SEND) {
                val shareUri = intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri
                val cacheImageUri = mainActivityViewModel.createImageFile()

                openUCrop(shareUri, cacheImageUri)
            } else if (intent.action == Intent.ACTION_EDIT && intent.data != null) {
                val data = intent.data
                if (intent.type.indexOf("image/*") != -1) {
                    val cacheImageUri = mainActivityViewModel.createImageFile()

                    openUCrop(data, cacheImageUri)
                }
            } else if (intent.data != null) {
                val data = intent.data
                if (intent.type.indexOf("image/*") != -1) {
                    val cacheImageUri = mainActivityViewModel.createImageFile()

                    openUCrop(data, cacheImageUri)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_UCROP -> {
                if (resultCode != UCrop.RESULT_ERROR && data != null) {
                    val cacheImageUri: Uri = UCrop.getOutput(data)!!
                    //get compress format from ucrop extras
                    val compressFormat = when (UCrop.getOutputImageFormat(data)) {
                        1 -> "png"
                        2 -> "webp"
                        else -> "jpg"
                    }

                    //copy cache file to storage and display
                    val finalImageUri = mainActivityViewModel.copyCacheImage(cacheImageUri, compressFormat)
                    galleryAddPic(finalImageUri)
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra("result", finalImageUri)
                    intent.putExtra("height", UCrop.getOutputImageHeight(data))
                    intent.putExtra("width", UCrop.getOutputImageWidth(data))
                    intent.putExtra("extension", compressFormat)
                    startActivity(intent)
                }
            }
            REQUEST_CODE_GALLERY_RESULT -> {
                if (data != null) {
                    val galleryImageUri = Uri.parse(data.data.toString())
                    val cacheImageUri = mainActivityViewModel.createImageFile()

                    openUCrop(galleryImageUri, cacheImageUri)
                }
            }
            REQUEST_CODE_CAMERA -> {
                val cacheImageUri = mainActivityViewModel.createImageFile()
                openUCrop(mainActivityViewModel.cameraImageUri, cacheImageUri)
            }
        }
    }

    /**
     * open camera through camera
     */
    private fun openCamera() {
        val imageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (imageIntent.resolveActivity(packageManager) != null) {
            mainActivityViewModel.createCameraImageFile(getString(R.string.file_provider))
            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, mainActivityViewModel.cameraImageUri)
            startActivityForResult(imageIntent, REQUEST_CODE_CAMERA)
        }
    }

    /**
     * open gallery through intent
     */
    private fun openGalleryImageSelection() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, REQUEST_CODE_GALLERY_RESULT)
    }

    /**
     * open ucrop activity
     * @param inputImageUri URI for the input file
     * @param outputImageUri URI for the output file
     */
    private fun openUCrop(inputImageUri: Uri, outputImageUri: Uri) {
        //ucrop ui setup
        setUCropOptions()
        UCrop.of(inputImageUri, outputImageUri)
                .withOptions(uCropOptions)
                .start(this, REQUEST_CODE_UCROP)
    }

    private fun galleryAddPic(path: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(path)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        this.sendBroadcast(mediaScanIntent)
    }

    private fun setUCropOptions() {
        uCropOptions.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
        uCropOptions.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        uCropOptions.setLogoColor(ContextCompat.getColor(this, R.color.colorPrimary))
        uCropOptions.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorPrimary))
        uCropOptions.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.grey_50))
        uCropOptions.setFreeStyleCropEnabled(true)
    }

    private lateinit var mainActivityViewModel: MainActivityViewModel

    companion object {
        val TAG: String = this::class.java.simpleName
        const val REQUEST_CODE_UCROP = 2
        const val REQUEST_CODE_GALLERY_RESULT = 3
        const val REQUEST_CODE_CAMERA = 4

        val uCropOptions = UCrop.Options()
    }
}
