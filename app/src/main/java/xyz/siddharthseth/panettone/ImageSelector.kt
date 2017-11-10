package xyz.siddharthseth.panettone


import android.os.Bundle
import android.app.Fragment
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.provider.MediaStore
import android.app.Activity.RESULT_OK
import android.app.FragmentManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import java.io.IOException
import android.content.ContentResolver




class ImageSelector : Fragment() {

    lateinit var fview: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fview = inflater.inflate(R.layout.fragment_image_selector, container, false)

        val imageSelector : ImageView = fview.findViewById(R.id.image_select)
        imageSelector.setOnClickListener {
            val intent = Intent();
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)

            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }

        val button: Button = fview.findViewById(R.id.button)
        button.setOnClickListener {
            val fragmentManager : FragmentManager = activity.fragmentManager
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, ImageConvertSettings())
                    .commit()
        }

        return fview
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.data != null) {

            val uri = data.data

            try {

                val contentResolver = context.contentResolver
                var inputStream = contentResolver.openInputStream(uri)

                var options : BitmapFactory.Options = BitmapFactory.Options();
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream,null,options)
                Log.v("nero", options.outHeight.toString())

                val scaleFactor = calculateScaleFactor(options)

                options = BitmapFactory.Options();
                options.inSampleSize = scaleFactor

                inputStream = contentResolver.openInputStream(uri)
                val bitmap=BitmapFactory.decodeStream(inputStream,null,options)

                Log.v("nero", options.outHeight.toString())
                val imageView: ImageView = fview.findViewById(R.id.imageView3)
                imageView.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun calculateScaleFactor(options: BitmapFactory.Options) : Int {
        var scaleFactor: Int

        val imageView: ImageView = fview.findViewById(R.id.imageView3)

        if(options.outWidth>options.outHeight){
            scaleFactor=((options.outWidth/imageView.width))
        }else{
            scaleFactor=((options.outHeight/imageView.height))
        }

        Log.v("nero", "scale to "+scaleFactor.toString())

        return scaleFactor
    }
}
