package com.example.firebasestorage.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasestorage.R
import kotlinx.android.synthetic.main.activity_doc_viewer.*


class DocViewerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doc_viewer)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val file = intent.getStringExtra("File")!!
        val fileName = intent.getStringExtra("FileName")!!

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}