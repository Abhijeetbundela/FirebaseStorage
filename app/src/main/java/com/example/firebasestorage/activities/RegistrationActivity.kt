package com.example.firebasestorage.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasestorage.R
import com.example.firebasestorage.dialog.LoadingDialog
import com.example.firebasestorage.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_registration.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class RegistrationActivity : AppCompatActivity() {

    private val mAuth by lazy { FirebaseAuth.getInstance() }

    private val loadingDialog by lazy { LoadingDialog().apply { isCancelable = false } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sign_up_btn.setOnClickListener {

            val email: String =
                email.editText!!.text.toString().trim { it <= ' ' }
            val password: String =
                password.editText!!.text.toString().trim { it <= ' ' }
            val confirmPassword: String =
                confirm_password.editText!!.text.toString().trim { it <= ' ' }

            when {
                email.isBlank() -> {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Enter Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                password.isBlank() -> {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Enter Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                confirmPassword.isBlank() -> {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Confirm Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                confirmPassword != password -> {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Password does not match",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    loadingDialog.show(supportFragmentManager, "base_loading_dialog")
                    registerUser(email, password)
                }
            }

        }
    }

    private fun registerUser(
        email: String,
        password: String
    ) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {

            loadingDialog.dismiss()

            val intent =
                Intent(Intent(this@RegistrationActivity, UserProfileActivity::class.java))
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

            intent.putExtra("register", true)

            startActivity(intent)

        }.addOnFailureListener {
            Toast.makeText(this@RegistrationActivity, it.localizedMessage, Toast.LENGTH_SHORT)
                .show()
            loadingDialog.dismiss()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}