package com.example.cah_cinema.util

import android.content.Context
import android.net.Uri
import com.example.cah_cinema.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

/**
 * Utility để upload ảnh lên Cloudinary.
 * Cloud name và upload preset được đọc từ BuildConfig (local.properties).
 */
object CloudinaryUploader {

    private val client = OkHttpClient()

    private val cloudName: String get() = BuildConfig.CLOUDINARY_CLOUD_NAME
    private val uploadPreset: String get() = BuildConfig.CLOUDINARY_UPLOAD_PRESET

    /**
     * Upload ảnh từ Uri lên Cloudinary.
     * @return URL của ảnh đã upload, hoặc null nếu thất bại.
     */
    suspend fun uploadImage(context: Context, imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Copy Uri vào file tạm
            val tempFile = createTempFileFromUri(context, imageUri)
                ?: return@withContext Result.failure(Exception("Không thể đọc file ảnh"))

            val uploadUrl = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    tempFile.name,
                    tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                )
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            // Xóa file tạm
            tempFile.delete()

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val secureUrl = json.getString("secure_url")
                Result.success(secureUrl)
            } else {
                Result.failure(Exception("Upload thất bại: ${response.code} - $responseBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createTempFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val extension = getFileExtension(context, uri)
            val tempFile = File.createTempFile("upload_", ".$extension", context.cacheDir)
            FileOutputStream(tempFile).use { output ->
                inputStream.copyTo(output)
            }
            inputStream.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    private fun getFileExtension(context: Context, uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri)
        return when (mimeType) {
            "image/jpeg" -> "jpg"
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/gif" -> "gif"
            else -> "jpg"
        }
    }
}
