package com.example.firebasestorage.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasestorage.R
import com.example.firebasestorage.dialog.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private val loadingDialog by lazy { LoadingDialog().apply { isCancelable = false } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAuth = FirebaseAuth.getInstance()

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

    private fun registerUser(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {

            Toast.makeText(this@RegistrationActivity, "Registration Complete", Toast.LENGTH_SHORT)
                .show()

            val intent =
                Intent(Intent(this@RegistrationActivity, HomeActivity::class.java))
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
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