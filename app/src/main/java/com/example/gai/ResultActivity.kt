package com.example.gai

import android.app.Activity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.io.IOException

class ResultActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create a ScrollView to enable scrolling
        val scrollView = ScrollView(this)

        // Create a LinearLayout to hold the TextView and apply padding
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 150, 50, 50)  // Added bottom padding as well for better spacing
        }

        // Create a TextView to show the message
        val messageView = TextView(this).apply {
            textSize = 20f
            val message = intent.getStringExtra("response_message") ?: "No message received."
            text = getMessageFromFile(message)
        }

        // Add the TextView to the layout
        layout.addView(messageView)

        // Add the layout to the ScrollView
        scrollView.addView(layout)

        // Set the ScrollView as the content view
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
