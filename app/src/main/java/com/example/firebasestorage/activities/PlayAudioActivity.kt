package com.example.firebasestorage.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasestorage.R
import com.example.jean.jcplayer.JcPlayerManagerListener
import com.example.jean.jcplayer.general.JcStatus
import com.example.jean.jcplayer.model.JcAudio
import com.example.jean.jcplayer.view.JcPlayerView


class PlayAudioActivity : AppCompatActivity(), JcPlayerManagerListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_audio)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val fileName = intent.getStringExtra("FileName")!!
        val url = intent.getStringExtra("File")!!

        val jcPlayerView = findViewById<JcPlayerView>(R.id.jc_player)

        val jcAudios: ArrayList<JcAudio> = ArrayList()
        jcAudios.add(JcAudio.createFromURL(fileName, url))

        jcPlayerView.initPlaylist(jcAudios, this)

        jcPlayerView.createNotification()

        jcPlayerView.createNotification(R.drawable.ic_audio_track_24)

    }

    override fun onCompletedAudio() {
        TODO("Not yet implemented")
    }

    override fun onContinueAudio(status: JcStatus) {
        TODO("Not yet implemented")
    }

    override fun onJcpError(throwable: Throwable) {
        TODO("Not yet implemented")
    }

    override fun onPaused(status: JcStatus) {
        TODO("Not yet implemented")
    }

    override fun onPlaying(status: JcStatus) {
        TODO("Not yet implemented")
    }

    override fun onPreparedAudio(status: JcStatus) {
        TODO("Not yet implemented")
    }

    override fun onStopped(status: JcStatus) {
    }

    override fun onTimeChanged(status: JcStatus) {
        TODO("Not yet implemented")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}