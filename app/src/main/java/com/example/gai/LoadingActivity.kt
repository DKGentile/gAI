package com.example.gai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ProgressBar
import java.io.File
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.view.Gravity
import java.io.IOException

class LoadingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(50, 150, 50, 50)
        }

        // Load image from assets
        val imageView = ImageView(this).apply {
            try {
                val inputStream = assets.open("gAItest1.png")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                setImageBitmap(bitmap)
                layoutParams = LinearLayout.LayoutParams(
                    400, // width
                    400  // height
                ).apply {
                    bottomMargin = 50
                    gravity = Gravity.CENTER
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        // Progress bar (indeterminate spinner)
        val progressBar = ProgressBar(this).apply {
            isIndeterminate = true
        }

        layout.addView(imageView)
        layout.addView(progressBar)
        setContentView(layout)

        // Upload logic
        val imagePath = intent.getStringExtra("image_path")
        if (imagePath != null) {
            val file = File(imagePath)
            ApiUploader.uploadImage(file,
                onSuccess = { result ->
                    runOnUiThread {
                        val intent = Intent(this, ResultActivity::class.java)
                        intent.putExtra("response_message", result)
                        startActivity(intent)
                        finish()
                    }
                },
                onError = { error ->
                    runOnUiThread {
                        val errorMessage = "Error: ${error.message}"
                        val intent = Intent(this, ResultActivity::class.java)
                        intent.putExtra("response_message", errorMessage)
                        startActivity(intent)
                        finish()
                    }
                }
            )
        } else {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("response_message", "Error: No image path provided.")
            startActivity(intent)
            finish()
        }
    }
}
