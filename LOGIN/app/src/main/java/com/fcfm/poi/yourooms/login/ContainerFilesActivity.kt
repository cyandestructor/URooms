package com.fcfm.poi.yourooms.login

import android.app.DownloadManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.fcfm.poi.yourooms.login.data.models.File
import com.fcfm.poi.yourooms.login.data.models.dao.FileDao
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContainerFilesActivity : AppCompatActivity() {
    private var containerId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_files)

        containerId = intent.getStringExtra("containerId")

        if (containerId != null) {
            displayContainerFiles(containerId!!)
        }
    }

    private fun displayContainerFiles(containerId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val files = FileDao().getContainerFiles(containerId)
            if (files.isNotEmpty()) {
                val file = files[0]

                withContext(Dispatchers.Main) {
                    when(file.contentType?.substringBefore('/')) {
                        "image" -> showImage(file)
                        else -> showFile(file)
                    }
                }

            }
        }
    }

    private fun showImage(file: File) {
        findViewById<Button>(R.id.download_button).visibility = View.GONE

        val imageView = findViewById<ImageView>(R.id.image_container)
        imageView.visibility = View.VISIBLE

        Picasso.get()
            .load(file.url)
            .placeholder(R.drawable.avatar_placeholder)
            .error(R.drawable.avatar_placeholder)
            .into(imageView)
    }

    private fun showFile(file: File) {
        findViewById<ImageView>(R.id.image_container).visibility = View.GONE
        val button = findViewById<Button>(R.id.download_button)

        button.setOnClickListener {
            val request = DownloadManager.Request(Uri.parse(file.url!!))
                .setTitle(file.name ?: "Download")
                .setDescription("Downloading...")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)

            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

            dm.enqueue(request)
        }

        button.text = file.name

        button.visibility = View.VISIBLE
    }
}