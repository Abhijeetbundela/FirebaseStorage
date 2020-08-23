package com.example.firebasestorage.activities

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.firebasestorage.R
import com.example.firebasestorage.dialog.LoadingDialog
import com.example.firebasestorage.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_user_profile.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

class UserProfileActivity : AppCompatActivity() {

    private var profileUri = "default"

    private lateinit var thumbByte: ByteArray

    private val mStorageRef by lazy { FirebaseStorage.getInstance().reference }

    private val mAuth by lazy { FirebaseAuth.getInstance().currentUser }

    private val userId by lazy { FirebaseAuth.getInstance().uid!! }

    private val database by lazy { FirebaseFirestore.getInstance() }

    private val loadingDialog by lazy { LoadingDialog().apply { isCancelable = false } }

    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if (!intent.hasExtra("register")) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        loadingDialog.show(supportFragmentManager, "base_loading_dialog")

        database.collection("users").document(userId).get().addOnSuccessListener { snapshot ->

            if (snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                name.editText!!.setText(user!!.userName)
                about.editText!!.setText(user.userAbout)

                Glide.with(this)
                    .load(user.profileUri)
                    .placeholder(R.drawable.default_user)
                    .into(profile_image)
            }

            upload_user_btn.text = "Update"
            loadingDialog.dismiss()
        }.addOnFailureListener {
            Toast.makeText(this@UserProfileActivity, it.localizedMessage, Toast.LENGTH_SHORT)
                .show()
            loadingDialog.dismiss()
        }

        profile_image.setOnClickListener {
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .start(this)
        }

        upload_user_btn.setOnClickListener {
            val name: String =
                name.editText!!.text.toString().trim { it <= ' ' }

            if (name.isBlank()) {
                Toast.makeText(
                    this@UserProfileActivity,
                    "Enter Name",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                uploadImages()
            }

        }

    }

    private fun addUser() {

        val name = name.editText!!.text.toString().trim()
        var about = about.editText!!.text.toString().trim()

        if (about == "") {
            about = "."
        }

        val userObject = User(userId, mAuth!!.email!!, name, about, profileUri)

        database.collection("users")
            .document(userId)
            .set(userObject)
            .addOnSuccessListener {
                val intent =
                    Intent(Intent(this@UserProfileActivity, HomeActivity::class.java))
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

                loadingDialog.dismiss()

            }.addOnFailureListener {
                loadingDialog.dismiss()
                Toast.makeText(this@UserProfileActivity, it.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun uploadImages() {

        profile_image.isEnabled = false
        name.isEnabled = false
        about.isEnabled = false
        upload_user_btn.isEnabled = false
        progress_bar.visibility = View.VISIBLE

        if (uri != null) {

            val thumbFile = File(uri!!.path!!)

            try {
                val thumbBitmap = Compressor(this)
                    .setMaxWidth(200)
                    .setMaxHeight(200)
                    .setQuality(50)
                    .compressToBitmap(thumbFile)

                val byteArray = ByteArrayOutputStream()

                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArray)
                thumbByte = byteArray.toByteArray()

            } catch (e: IOException) {
                e.printStackTrace()
            }

            val picRef = mStorageRef.child("profileImages/$userId.jpg")

            picRef.putFile(uri!!).addOnCompleteListener { pic ->
                if (pic.isSuccessful) {
                    picRef.downloadUrl.addOnCompleteListener { picD ->
                        if (picD.isSuccessful) {
                            profileUri = picD.result.toString()
                            addUser()
                        } else {
                            addUser()
                        }
                    }
                } else {
                    addUser()
                }

            }.addOnProgressListener { taskSnapshot ->
                val progress = 100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount

                println("Upload is $progress% done")
                val currentProgress = progress.toInt()
                progress_bar.progress = currentProgress

                upload_user_btn.text = "Uploading $currentProgress% is done"

                if (currentProgress == 100) {

                    upload_user_btn.text = "Uploading Complete"
                    progress_bar.visibility = View.GONE
                    loadingDialog.show(supportFragmentManager, "base_loading_dialog")
                }

            }
        } else {
            addUser()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {
                uri = result.uri
                profile_image.setImageURI(uri)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this@UserProfileActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}