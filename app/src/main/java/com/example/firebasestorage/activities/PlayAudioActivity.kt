package com.example.firebasestorage.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasestorage.R
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView


class PlayAudioActivity : AppCompatActivity() {

    private lateinit var jcPlayerView: JcPlayerView
    private val jcAudios: ArrayList<JcAudio> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_audio)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fileName = intent.getStringExtra("FileName")!!
        val url = intent.getStringExtra("File")!!

        jcPlayerView = findViewById(R.id.jc_player)
        jcAudios.add(JcAudio.createFromURL(fileName, url))

        jcPlayerView.initPlaylist(jcAudios, null)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStop() {
        jcPlayerView.kill()
        super.onStop()
    }

    override fun onPause() {
        jcPlayerView.kill()
        super.onPause()
    }
}