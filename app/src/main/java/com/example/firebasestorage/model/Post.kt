package com.example.firebasestorage.model

import com.google.firebase.database.Exclude
import java.util.*

data class Post(
    val userId: String = "",
    val userName: String = "",
    val description: String = "",
    val fileType: String = "",
    val file: String = "",
    val fileName: String = "",
    val downloadFile: String = ""
) {
    @Exclude
    fun toMap(): Map<String, Any>? {
        val result =
            HashMap<String, Any>()
        result["userId"] = userId
        result["userName"] = userName
        result["description"] = description
        result["fileType"] = fileType
        result["file"] = file
        result["fileName"] = fileName
        result["downloadFile"] = downloadFile
        return result
    }
}