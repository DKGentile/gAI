package com.example.gai

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.IOException
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.content.Intent
import android.net.Uri
import android.widget.Button
import android.graphics.drawable.GradientDrawable



class ResultActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scrollView = ScrollView(this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 150, 50, 50)
        }

        // ImageView at the top
        val imageView = ImageView(this).apply {
            val assetManager = assets
            try {
                val inputStream = assetManager.open("gAItest1.png")
                val bitmap = BitmapFactory.decodeStream(inputStream)
                setImageBitmap(bitmap)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    400
                ).apply {
                    bottomMargin = 30
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        layout.addView(imageView)

        // Message TextView
        val responseMessage = intent.getStringExtra("response_message") ?: "No message received."
        val messageView = TextView(this).apply {
            textSize = 20f
            text = getMessageFromFile(responseMessage)
        }
        layout.addView(messageView)

        // Treatment recommendation logic
        if (responseMessage.contains("Blight", ignoreCase = true) ||
            responseMessage.contains("Mildew", ignoreCase = true)) {

            // "Recommended Treatment:" bold label
            val treatmentLabel = TextView(this).apply {
                text = "Recommended Treatment:"
                textSize = 18f
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setPadding(0, 40, 0, 10)
            }
            layout.addView(treatmentLabel)

            // Treatment button
            // Treatment button with rounded white background and black text
            val treatmentButton = Button(this).apply {
                text = if (responseMessage.contains("Blight", ignoreCase = true)) {
                    "Fungicide Treatment"
                } else {
                    "Anti-Mildew Treatment"
                }

                setTextColor(android.graphics.Color.BLACK)

                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 40f
                    setColor(android.graphics.Color.WHITE)
                    setStroke(2, android.graphics.Color.BLACK)
                }

                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 20
                }

                setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse("https://www.amazon.com/s?k=fungicide")
                    }
                    startActivity(intent)
                }
            }

            layout.addView(treatmentButton)
        }

        scrollView.addView(layout)
        setContentView(scrollView)
    }



    // Function to load content from relevant .txt file based on the response message
    private fun getMessageFromFile(response: String): String {
        return when {
            response.contains("Healthy", ignoreCase = true) -> loadFileContent("healthy.txt")
            response.contains("Blight", ignoreCase = true) -> loadFileContent("blight.txt")
            response.contains("Mildew", ignoreCase = true) -> loadFileContent("mildew.txt")
            else -> loadFileContent("error.txt")
        }
    }

    // Helper function to read content from the assets folder
    private fun loadFileContent(fileName: String): String {
        return try {
            val inputStream = assets.open(fileName)
            inputStream.bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            "Error reading file: $fileName"
        }
    }
}
