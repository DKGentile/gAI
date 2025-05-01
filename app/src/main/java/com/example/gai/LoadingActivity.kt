package com.example.gai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import java.io.File

class LoadingActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // UI: Simple layout with a loading indicator
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 150, 50, 50)
            gravity = android.view.Gravity.CENTER
        }

        val loadingText = TextView(this).apply {
            text = "Uploading image, please wait..."
            textSize = 18f
        }

        val progressBar = ProgressBar(this).apply {
            isIndeterminate = true
        }

        layout.addView(loadingText)
        layout.addView(progressBar)
        setContentView(layout)

        // Get the image path from the Intent
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
