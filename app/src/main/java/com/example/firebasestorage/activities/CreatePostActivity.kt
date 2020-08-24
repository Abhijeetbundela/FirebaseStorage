package com.example.firebasestorage.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.firebasestorage.R
import com.example.firebasestorage.dialog.LoadingDialog
import com.example.firebasestorage.model.Post
import com.example.firebasestorage.model.User
import com.example.firebasestorage.utils.FileNameUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_create_post.*
import java.util.*


class CreatePostActivity : AppCompatActivity(), View.OnClickListener {

    private var status = false

    private val PDF: Int = 0
    private val DOC: Int = 1
    private val AUDIO: Int = 2
    private val VIDEO: Int = 3
    private val IMAGE: Int = 4

    private var resultData: Uri? = null

    private var fileType: String = ""
    private var fileName: String = ""
    private var downloadFile: String = ""

    private var fileData = "default"

    private val mStorageRef by lazy { FirebaseStorage.getInstance().reference }

    private val firebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val mDatabase by lazy { FirebaseDatabase.getInstance().reference }

    private val loadingDialog by lazy { LoadingDialog().apply { isCancelable = false } }

    private val userId by lazy { FirebaseAuth.getInstance().uid!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        select_file.setOnClickListener(this)

        select_image.setOnClickListener(this)
        select_pdf.setOnClickListener(this)
        select_audio.setOnClickListener(this)
        select_video.setOnClickListener(this)
        select_document.setOnClickListener(this)
        upload_btn.setOnClickListener(this)

    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.select_file -> {
                if (status) {
                    select_file_layout.visibility = View.GONE
                    view.visibility = View.GONE
                    status = false
                } else {
                    select_file_layout.visibility = View.VISIBLE
                    view.visibility = View.VISIBLE
                    status = true
                }

            }

            R.id.select_image -> {
                val galleryIntent = Intent()
                galleryIntent.action = Intent.ACTION_PICK
                galleryIntent.type = "image/*"
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), IMAGE)
            }

            R.id.select_pdf -> {
                val intent = Intent()
                intent.type = "application/pdf"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF)
            }

            R.id.select_audio -> {
                val intent = Intent()
                intent.type = "audio/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Audio"), AUDIO)
            }

            R.id.select_video -> {
                val intent = Intent()
                intent.type = "video/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO)
            }

            R.id.select_document -> {
                val intent = Intent()
                intent.type =
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                intent.action = Intent.ACTION_OPEN_DOCUMENT
                startActivityForResult(Intent.createChooser(intent, "Select DOC"), DOC)

            }

            R.id.upload_btn -> {

                val description: String =
                    description_input.editText!!.text.toString().trim { it <= ' ' }

                when {
                    description.isBlank() -> {
                        Toast.makeText(
                            this@CreatePostActivity,
                            "Enter Description",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    resultData == null -> {
                        Toast.makeText(
                            this@CreatePostActivity,
                            "Choose file",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        uploadFile(fileType, description)
                    }
                }
            }
        }
    }

    private fun uploadFile(fileType: String, description: String) {

        val path = System.currentTimeMillis().toString() + "." + FileNameUtils.getFileExtension(
            resultData!!,
            contentResolver
        )

        val postPdfRef = mStorageRef.child(
            "postPDFs/$path"
        )
        val postDocRef = mStorageRef.child(
            "postDocs/$path"
        )
        val postAudioRef = mStorageRef.child(
            "postAudios/$path"
        )
        val postVideoRef = mStorageRef.child(
            "postVideos/$path"
        )

        val imagePath = path.replace("null", "jpg")

        val postImageRef = mStorageRef.child(
            "postImages/$imagePath"
        )

        when (fileType) {
            "PDF" -> {
                uploadPostData(postPdfRef, description)
            }
            "DOC" -> {
                uploadPostData(postDocRef, description)
            }
            "AUDIO" -> {
                uploadPostData(postAudioRef, description)
            }
            "VIDEO" -> {
                uploadPostData(postVideoRef, description)
            }
            "IMAGE" -> {
                uploadPostData(postImageRef, description)
            }

        }

    }

    private fun uploadPostData(ref: StorageReference, description: String) {
        disableUI()
        post_progress_bar.visibility = View.VISIBLE

        ref.putFile(resultData!!).addOnCompleteListener { pic ->
            if (pic.isSuccessful) {
                ref.downloadUrl.addOnCompleteListener { picD ->

                    ref.metadata.addOnSuccessListener {
                        downloadFile = it.name!!
                    }

                    if (picD.isSuccessful) {
                        fileData = picD.result.toString()
                        writePost(description)
                    }
                }
            }
        }.addOnProgressListener { taskSnapshot ->
            val progress = 100 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount
            val currentProgress = progress.toInt()
            post_progress_bar.progress = currentProgress

            upload_btn.text = "Uploading $currentProgress% is done"

            if (currentProgress == 100) {
                upload_btn.text = "Uploading Complete"
                post_progress_bar.visibility = View.GONE
                loadingDialog.show(supportFragmentManager, "base_loading_dialog")
            }

        }
    }

    private fun writePost(description: String) {

        firebaseFirestore.collection("users").document(userId).get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)!!

                    val key: String = mDatabase.child("posts").push().key!!
                    val post =
                        Post(
                            user.userId,
                            user.userName,
                            description,
                            fileType,
                            fileData,
                            fileName,
                            downloadFile
                        )

                    val postValues = post.toMap()

                    val childUpdates: HashMap<String, Any> = HashMap()

                    postValues?.let { childUpdates.put("/posts/$key", it) }
                    postValues?.let { childUpdates.put("/user-posts/$userId/$key", it) }

                    mDatabase.updateChildren(childUpdates).addOnSuccessListener {
                        loadingDialog.dismiss()
                        Toast.makeText(this@CreatePostActivity, "Post Added", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }

                }

            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        refreshUI()

        if (resultCode == RESULT_OK) {
            resultData = data!!.data
            when (requestCode) {
                0 -> {
                    fileName = FileNameUtils.fileName(resultData, contentResolver)
                    fileType = "PDF"
                    enableUI(R.drawable.ic_picture_as_pdf, fileName)
                }
                1 -> {
                    fileName = FileNameUtils.fileName(resultData, contentResolver)
                    fileType = "DOC"
                    enableUI(R.drawable.ic_attach_file_24, fileName)
                }
                2 -> {
                    fileName = FileNameUtils.fileName(resultData, contentResolver)
                    fileType = "AUDIO"
                    enableUI(R.drawable.ic_audio_track_24, fileName)
                }
                3 -> {
                    fileName = FileNameUtils.fileName(resultData, contentResolver)
                    fileType = "VIDEO"
                    enableUI(R.drawable.ic_video_24, fileName)
                }
            }
        }

        if (requestCode == IMAGE && resultCode == RESULT_OK) {
            val imageUri = data!!.data
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            fileType = "IMAGE"
            resultData = CropImage.getActivityResult(data).uri
            if (resultCode == Activity.RESULT_OK) {
                selected_image.visibility = View.VISIBLE
                selected_image.setImageURI(resultData)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = CropImage.getActivityResult(data).error
                Log.d("Error", error.toString())
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun enableUI(drawable: Int, fileName: String) {
        created_file.visibility = View.VISIBLE
        created_file.setCompoundDrawablesWithIntrinsicBounds(
            0,
            drawable,
            0,
            0
        )
        created_file.text = fileName
    }

    private fun refreshUI() {
        resultData = null
        selected_image.visibility = View.GONE
    }

    private fun disableUI() {
        description_input.isEnabled = false
        select_file.isEnabled = false
        select_image.isEnabled = false
        select_audio.isEnabled = false
        select_video.isEnabled = false
        select_pdf.isEnabled = false
        select_document.isEnabled = false
        upload_btn.isEnabled = false
        selected_image.isEnabled = false
    }

}