package com.example.gai

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var mainLayout: ConstraintLayout
    private lateinit var imageView: ImageView
    private lateinit var btnTakePicture: Button
    private lateinit var confirmLayout: LinearLayout
    private lateinit var btnConfirm: Button
    private lateinit var btnRetake: Button
    private var capturedImage: Bitmap? = null

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                capturedImage = it
                imageView.setImageBitmap(it)
                imageView.visibility = View.VISIBLE
                confirmLayout.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main)  // Get the root layout

        // Remove any existing child views (to avoid duplicates)
        mainLayout.removeAllViews()

        // Create ImageView dynamically
        imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            )
            visibility = View.GONE
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        mainLayout.addView(imageView)

        // Create Take Picture Button
        btnTakePicture = Button(this).apply {
            text = "Take Picture"
            setOnClickListener {
                if (checkCameraPermission()) {
                    openCamera()
                } else {
                    requestCameraPermission()
                }
            }
        }
        mainLayout.addView(btnTakePicture)

        // Create Confirmation Layout (LinearLayout)
        confirmLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            visibility = View.GONE
        }

        // Create Confirm Button
        btnConfirm = Button(this).apply {
            text = "Confirm"
            setOnClickListener {
                Toast.makeText(this@MainActivity, "Picture Confirmed!", Toast.LENGTH_SHORT).show()
            }
        }

        // Create Retake Button
        btnRetake = Button(this).apply {
            text = "Retake"
            setOnClickListener {
                confirmLayout.visibility = View.GONE
                imageView.visibility = View.GONE
                imageView.setImageBitmap(null)
            }
        }

        // Add buttons to confirmation layout
        confirmLayout.addView(btnConfirm)
        confirmLayout.addView(btnRetake)

        // Add confirmation layout to main layout
        mainLayout.addView(confirmLayout)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    // User denied permission without selecting "Don't ask again"
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                } else {
                    // User permanently denied permission (selected "Don't ask again")
                    showPermissionDeniedDialog()
                }
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Denied")
            .setMessage("You have permanently denied the camera permission. Please go to settings to allow it.")
            .setPositiveButton("Go to Settings") { _, _ ->
                // Open app settings to allow the user to change permission
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = android.net.Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
    }
}
