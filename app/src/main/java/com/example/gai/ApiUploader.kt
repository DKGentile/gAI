package com.example.gai

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

object ApiUploader {
    private const val UPLOAD_URL = "https://yourapi.com/upload"

    /**
     * Uploads the image file using a multipart/form-data POST request.
     */
    fun uploadImage(imageFile: File) {
        val client = OkHttpClient()

        // Build the file part using the extension function for RequestBody.
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image", imageFile.name,
                imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            .build()

        // Build the POST request.
        val request = Request.Builder()
            .url(UPLOAD_URL)
            .post(requestBody)
            .build()

        // Execute the request asynchronously.
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                // Optionally handle failure on the UI thread.
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // Using the Kotlin property extension for response body.
                    println("Upload successful: ${response.body?.string()}")
                } else {
                    // Using the Kotlin property extension for response message.
                    println("Upload failed: ${response.message}")
                }
            }
        })
    }
}
