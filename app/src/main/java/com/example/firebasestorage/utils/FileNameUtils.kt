package com.example.firebasestorage.utils

import android.app.DownloadManager
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.view.View
import android.webkit.MimeTypeMap
import com.example.firebasestorage.activities.DocViewerActivity
import com.example.firebasestorage.activities.PDFViewerActivity
import com.example.firebasestorage.activities.PlayAudioActivity
import com.example.firebasestorage.activities.PlayVideoActivity
import com.example.firebasestorage.fragment.AllPostFragment
import com.example.firebasestorage.fragment.PostFragment
import com.example.firebasestorage.model.Post
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object FileNameUtils {

    private val storageReference by lazy { FirebaseStorage.getInstance().reference }

    private var ref: StorageReference? = null

    fun fileName(resultData: Uri?, contentResolver: ContentResolver): String {
        val returnCursor: Cursor? =
            contentResolver.query(resultData!!, null, null, null, null)
        val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()

        return returnCursor.getString(nameIndex)
    }

    fun getFileExtension(uri: Uri, contentResolver: ContentResolver): String? {
        val cR = contentResolver
        val mime = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    fun downloadFile(
        model: Post,
        context: Context
    ) {

        when (model.fileType) {
            "PDF" -> {
                ref = storageReference.child("postPDFs/${model.downloadFile}")
                ref!!.downloadUrl.addOnSuccessListener {
                    downloadUri(context, Environment.DIRECTORY_DOWNLOADS, it, model.fileName)
                }
            }
            "DOC" -> {
                ref = storageReference.child("postDocs/${model.downloadFile}")
                ref!!.downloadUrl.addOnSuccessListener {
                    downloadUri(context, Environment.DIRECTORY_DOWNLOADS, it, model.fileName)
                }
            }
            "AUDIO" -> {
                ref = storageReference.child("postAudios/${model.downloadFile}")
                ref!!.downloadUrl.addOnSuccessListener {
                    downloadUri(context, Environment.DIRECTORY_DOWNLOADS, it, model.fileName)
                }
            }
            "VIDEO" -> {
                ref = storageReference.child("postVideos/${model.downloadFile}")
                ref!!.downloadUrl.addOnSuccessListener {
                    downloadUri(context, Environment.DIRECTORY_DOWNLOADS, it, model.fileName)
                }
            }
            "IMAGE" -> {
                ref = storageReference.child("postImages/${model.downloadFile}")
                ref!!.downloadUrl.addOnSuccessListener {
                    downloadUri(context, Environment.DIRECTORY_DOWNLOADS, it, model.fileName)
                }
            }
        }

    }

    fun setUIAllPost(
        drawable: Int,
        holder: AllPostFragment.AllPostVM,
        model: Post,
        context: Context
    ) {
        holder.fileText.visibility = View.VISIBLE
        holder.fileText.text = model.fileName
        holder.fileText.setCompoundDrawablesWithIntrinsicBounds(
            0,
            drawable,
            0,
            0
        )

        when (model.fileType) {
            "PDF" -> {
                holder.fileText.setOnClickListener {
                    val intent = Intent(context, PDFViewerActivity::class.java)
                    intentPost(model.fileName, intent, model.file, context)
                }
            }
            "DOC" -> {
                holder.fileText.setOnClickListener {
                    val intent = Intent(context, DocViewerActivity::class.java)
                    intentPost(model.fileName, intent, model.file, context)
                }
            }
            "AUDIO" -> {
                holder.fileText.setOnClickListener {
                    val intent = Intent(context, PlayAudioActivity::class.java)
                    intentPost(model.fileName, intent, model.file, context)
                }
            }
            "VIDEO" -> {
                holder.fileText.setOnClickListener {
                    val intent = Intent(context, PlayVideoActivity::class.java)
                    intentPost(model.fileName, intent, model.file, context)
                }
            }
        }

    }

    fun setUIPost(
        drawable: Int,
        holder: PostFragment.PostVH,
        model: Post,
        context: Context
    ) {
        holder.fileText.visibility = View.VISIBLE
        holder.fileText.text = model.fileName
        holder.fileText.setCompoundDrawablesWithIntrinsicBounds(
            0,
            drawable,
            0,
            0
        )

        when (model.fileType) {
            "PDF" -> {
                holder.fileText.setOnClickListener {
                    val intent = Intent(context, PDFViewerActivity::class.java)
                    intentPost(model.fileName, intent, model.file, context)
                }
            }
            "DOC" -> {
                holder.fileText.setOnClickListener {
                    val intent = Intent(context, DocViewerActivity::class.java)
                    intentPost(model.fileName, intent, model.file, context)
                }
            }
            "AUDIO" -> {
                holder.fileText.setOnClickListener {
                    val intent = Intent(context, PlayAudioActivity::class.java)
                    intentPost(model.fileName, intent, model.file, context)
                }
            }
            "VIDEO" -> {
                holder.fileText.setOnClickListener {
                    val intent = Intent(context, PlayVideoActivity::class.java)
                    intentPost(model.fileName, intent, model.file, context)
                }
            }
        }

    }

    private fun intentPost(
        fileName: String,
        intent: Intent,
        file: String,
        context: Context
    ) {
        intent.putExtra("FileName", fileName)
        intent.putExtra("File", file)
        context.startActivity(intent)
    }

    private fun downloadUri(
        context: Context,
        destinationDir: String,
        uri: Uri,
        fileName: String
    ) {
        val dm =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val request = DownloadManager.Request(uri)

        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or
                    DownloadManager.Request.NETWORK_MOBILE
        )
            .setAllowedOverRoaming(false).setTitle("Downloading...")

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context, destinationDir, fileName)
        request.setTitle(fileName)

        dm.enqueue(request)
    }
}