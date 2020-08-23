package com.example.firebasestorage.activities

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasestorage.R
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import kotlinx.android.synthetic.main.activity_play_video.*

class PlayVideoActivity : AppCompatActivity() {

    private lateinit var simpleExoPlayerView: PlayerView

    private lateinit var exoPlayer: SimpleExoPlayer

    private var isMute = false

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {

        if (event!!.keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            exoPlayer.volume = 1f
            mute_button.setImageResource(R.drawable.ic_volume_up)

        }

        return super.onKeyUp(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        simpleExoPlayerView = findViewById(R.id.simpleExoPlayerView)

        exoPlayer = SimpleExoPlayer.Builder(this@PlayVideoActivity).build()

        simpleExoPlayerView.player = exoPlayer

        full_screen.setOnClickListener {
            changeOrientation()
        }

        close_button.setOnClickListener {
            finish()
        }

        mute_button.setOnClickListener {

            if (isMute) {
                exoPlayer.volume = 1f
                mute_button.setImageResource(
                    R.drawable.ic_volume_up
                )
            } else {
                exoPlayer.volume = 0f
                mute_button.setImageResource(
                    R.drawable.ic_volume_off
                )
            }

            isMute = !isMute

        }

        goFullScreen()

        val url = intent.getStringExtra("url")!!
        playVideo(url)

    }

    private fun pausePlayer() {
        exoPlayer.playWhenReady = false
        exoPlayer.playbackState
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()

    }

    override fun onResume() {
        super.onResume()
        goFullScreen()
    }

    private fun changeOrientation() {
        val orientation = this.resources.configuration.orientation
        requestedOrientation = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun playVideo(url: String) {

        simpleExoPlayerView.setControllerVisibilityListener {

            if (it == View.VISIBLE) {
                top_layout.visibility = View.VISIBLE
                mute_button.visibility = View.VISIBLE
            } else if (it == View.GONE) {
                top_layout.visibility = View.GONE
                mute_button.visibility = View.GONE
            }

        }

        exoPlayer.playWhenReady = true

        val videoSource = ProgressiveMediaSource.Factory(
            DefaultHttpDataSourceFactory("okhttp/4.2.1")
        ).createMediaSource(Uri.parse(url))

        exoPlayer.prepare(videoSource)

        exoPlayer.addListener(object : Player.EventListener {
            override fun onTracksChanged(
                trackGroups: TrackGroupArray,
                trackSelections: TrackSelectionArray
            ) {
            }

            override fun onLoadingChanged(isLoading: Boolean) {}
            override fun onPlayerStateChanged(
                playWhenReady: Boolean,
                playbackState: Int
            ) {
            }

            override fun onPlayerError(error: ExoPlaybackException) {

                val data = hashMapOf(
                    "error" to error.localizedMessage,
                    "fullError" to error.message
                )

            }
        })

    }

    private fun goFullScreen() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}