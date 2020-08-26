package com.example.firebasestorage.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasestorage.R
import com.example.firebasestorage.dialog.LoadingDialog
import kotlinx.android.synthetic.main.activity_doc_viewer.*
import java.net.URLEncoder


class DocViewerActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_viewer)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val file = intent.getStringExtra("File")!!
        file_name.text = intent.getStringExtra("FileName")!!

        val url = "https://docs.google.com/gview?embedded=true&url=" + URLEncoder.encode(
            file,
            "ISO-8859-1"
        )

        web_view.visibility = View.VISIBLE
        web_view.settings.javaScriptEnabled = true;
        web_view.settings.pluginState = WebSettings.PluginState.ON
        web_view.webViewClient = Callback()
        web_view.settings.pluginState = WebSettings.PluginState.ON
        web_view.loadUrl(url)

    }


    private class Callback : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView, url: String
        ): Boolean {
            view.loadUrl(url)
            return true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}