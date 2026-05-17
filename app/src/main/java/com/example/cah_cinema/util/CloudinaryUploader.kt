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
 * Utility để upload ảnh/video lên Cloudinary.
 * Cloud name và upload preset được đọc từ BuildConfig (local.properties).
 */
object CloudinaryUploader {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(120, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private val cloudName: String get() = BuildConfig.CLOUDINARY_CLOUD_NAME
    private val uploadPreset: String get() = BuildConfig.CLOUDINARY_UPLOAD_PRESET

    /**
     * Upload ảnh từ Uri lên Cloudinary.
     * @return URL của ảnh đã upload, hoặc null nếu thất bại.
     */
    suspend fun uploadImage(context: Context, imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
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

    /**
     * Upload video từ Uri lên Cloudinary (resource_type = video).
     * @return URL của video đã upload, hoặc null nếu thất bại.
     */
    suspend fun uploadVideo(context: Context, videoUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            val tempFile = createTempFileFromUri(context, videoUri, isVideo = true)
                ?: return@withContext Result.failure(Exception("Không thể đọc file video"))

            val uploadUrl = "https://api.cloudinary.com/v1_1/$cloudName/video/upload"

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    tempFile.name,
                    tempFile.asRequestBody("video/*".toMediaTypeOrNull())
                )
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            tempFile.delete()

            if (response.isSuccessful && responseBody != null) {
                val json = JSONObject(responseBody)
                val secureUrl = json.getString("secure_url")
                Result.success(secureUrl)
            } else {
                Result.failure(Exception("Upload video thất bại: ${response.code} - $responseBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createTempFileFromUri(context: Context, uri: Uri, isVideo: Boolean = false): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val extension = if (isVideo) getVideoExtension(context, uri) else getFileExtension(context, uri)
            val prefix = if (isVideo) "video_upload_" else "upload_"
            val tempFile = File.createTempFile(prefix, ".$extension", context.cacheDir)
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

    private fun getVideoExtension(context: Context, uri: Uri): String {
        val mimeType = context.contentResolver.getType(uri)
        return when (mimeType) {
            "video/mp4" -> "mp4"
            "video/quicktime" -> "mov"
            "video/x-msvideo" -> "avi"
            "video/webm" -> "webm"
            else -> "mp4"
        }
    }
}
