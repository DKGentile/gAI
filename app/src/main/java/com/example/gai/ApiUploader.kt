package com.example.gai

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiUploader {

    private const val UPLOAD_URL = "http://107.20.111.24:5000/predict"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    fun uploadImage(
        file: File,
        onSuccess: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val mediaType = "image/*".toMediaTypeOrNull()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, file.asRequestBody(mediaType))
            .build()

        val request = Request.Builder()
            .url(UPLOAD_URL)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val body = response.body?.string()
                    if (body != null) {
                        onSuccess(body)
                    } else {
                        onError(IOException("Empty response body"))
                    }
                } else {
                    onError(IOException("Upload failed with code ${response.code}"))
                }
            }
        })
    }
}
