package com.example.firebasestorage.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.PRDownloaderConfig
import com.example.firebasestorage.R
import com.example.firebasestorage.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_pdf_viewer.*
import java.io.File

class PDFViewerActivity : AppCompatActivity() {

    private val loadingDialog by lazy { LoadingDialog().apply { isCancelable = false } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        loadingDialog.show(supportFragmentManager, "base_loading_dialog")

        val fileName = intent.getStringExtra("FileName")!!
        val url = intent.getStringExtra("File")!!

        file_name.text = fileName

        PRDownloader.initialize(this)

        val config = PRDownloaderConfig.newBuilder()
            .setReadTimeout(30000)
            .setConnectTimeout(30000)
            .build()

        PRDownloader.initialize(applicationContext, config)

        PRDownloader.download(url, cacheDir.toString(), "file")
            .build()
            .start(object : OnDownloadListener {
                override fun onDownloadComplete() {
                    loadFromUrl(File(cacheDir, "file"))
                }

                override fun onError(error: com.downloader.Error?) {
                    loadingDialog.dismiss()
                }
            })
    }

    private fun loadFromUrl(file: File) {

        pdfView.fromFile(file)
            .enableSwipe(true)
            .enableDoubletap(true)
            .defaultPage(0).enableAnnotationRendering(false)
            .password(null)
            .swipeHorizontal(true)
            .pageSnap(true)
            .autoSpacing(true)
            .pageFling(true)
            .onLoad {
                loadingDialog.dismiss()
            }
            .scrollHandle(null)
            .enableAntialiasing(true)
            .spacing(0)
            .load()
    }

}