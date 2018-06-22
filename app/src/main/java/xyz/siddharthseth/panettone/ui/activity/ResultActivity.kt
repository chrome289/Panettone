package xyz.siddharthseth.panettone.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_result.*
import xyz.siddharthseth.panettone.R
import xyz.siddharthseth.panettone.data.GlideApp
import java.io.File


class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initUi(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_result_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                shareImage()
            }
        }
        return true
    }

    /**
     * init ui
     * @param intent intent passed through uCrop
     */
    private fun initUi(intent: Intent) {
        val filePath = intent.getStringExtra("result")
        val fileWidth = intent.getIntExtra("width", 0)
        val fileHeight = intent.getIntExtra("height", 0)
        val fileExtension = intent.getStringExtra("extension")

        val file = File(filePath)
        fileUri = FileProvider.getUriForFile(this, getString(R.string.file_provider), file)

        fileName.text = file.name
        fileSize.text = getFileSize(file.length())
        fileResolution.text = "$fileWidth X $fileHeight"
        fileFormat.text = fileExtension.toUpperCase()

        openButton.setOnClickListener {
            openImage()
        }

        GlideApp.with(this)
                .load(filePath)
                .fitCenter()
                .into(imageView)
    }

    /**
     * get file size
     */
    private fun getFileSize(length: Long): String {
        //return formatted file size (bytes)
        return when {
            length < 1024 -> (length).toString() + " B"
            length < (1024 * 1024) -> (length / 1024).toString() + " KB"
            else -> (length / (1024 * 1024)).toString() + "." + String.format("%.0f",
                    (length % (1024 * 1024)) / (1024 * 10.24f)) + " MB"
        }
    }

    /**
     * share image
     */
    private fun shareImage() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        shareIntent.type = "image/*"
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    /**
     * open image
     */
    private fun openImage() {
        val openIntent = Intent()
        openIntent.action = Intent.ACTION_VIEW
        openIntent.setDataAndType(fileUri, "image/*")
        openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(openIntent)
    }

    private var fileUri: Uri = Uri.EMPTY
}
