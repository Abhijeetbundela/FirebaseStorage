package com.example.firebasestorage.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.firebasestorage.R
import kotlinx.android.synthetic.main.activity_full_image.*

class FullImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_image)

        val profilePhoto = intent.getStringExtra("profilePhoto")!!

        if (profilePhoto != "NA") {

            Glide.with(this@FullImageActivity)
                .load(profilePhoto)
                .placeholder(R.drawable.default_user)
                .into(userProfilePhoto)
        } else {
            userProfilePhoto.setImageResource(R.drawable.default_user)
        }

    }
}