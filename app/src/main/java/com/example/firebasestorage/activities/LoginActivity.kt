package com.example.firebasestorage.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasestorage.R
import com.example.firebasestorage.dialog.LoadingDialog
import com.example.firebasestorage.utils.PreferencesManagement
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val mAuth by lazy { FirebaseAuth.getInstance() }

    private val loadingDialog by lazy { LoadingDialog().apply { isCancelable = false } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sign_up_text_view.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
        }

        val userData = PreferencesManagement.getUserId(this)

        if (mAuth.currentUser != null && userData != null) {
            startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
            finish()
        } else if(mAuth.currentUser != null && userData == null){
            startActivity(Intent(this@LoginActivity, UserProfileActivity::class.java))
            finish()
        }

        login_btn.setOnClickListener {

            val email: String =
                login_email.editText!!.text.toString().trim { it <= ' ' }
            val password: String =
                login_password.editText!!.text.toString().trim { it <= ' ' }

            when {
                email.isBlank() -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Enter Email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                password.isBlank() -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Enter Password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    loadingDialog.show(supportFragmentManager, "base_loading_dialog")
                    loginUser(email, password)

                }
            }

        }


    }

    private fun loginUser(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

            loadingDialog.dismiss()

            PreferencesManagement.saveUserId(
                this@LoginActivity,
                mAuth.uid
            )

            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }.addOnFailureListener {
            loadingDialog.dismiss()
            Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }

    }
}