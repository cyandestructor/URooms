package com.fcfm.poi.yourooms.login.data.models.dao

import android.net.Uri
import com.fcfm.poi.yourooms.login.data.models.File
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class FileDao {
    private val storage = Firebase.storage
    private val db = Firebase.firestore

    suspend fun uploadFile(fileUri: Uri, directory: String) : File? {
        val sdf = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val filename = sdf.format(Date())

        val ref = storage.reference.child("${directory}/${filename}")

        return try {
            val result = ref.putFile(fileUri).await()

            val url = ref.downloadUrl.await()

            File(
                null, null, null,
                url.toString(), null,
                result.metadata?.contentType, Date()
            )
        }
        catch (e: Exception) {
            return null
        }
    }

    suspend fun addFile(file: File) : String? {
        val data = hashMapOf(
            "id" to file.id,
            "containerId" to file.containerId,
            "groupId" to file.groupId,
            "url" to file.url,
            "name" to file.name,
            "contentType" to file.contentType,
            "date" to file.date
        )

        return try {
            val result = db.collection("files")
                .add(data)
                .await()

            result.id
        }
        catch (e: Exception) {
            return null
        }
    }
}