package com.example.gai

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.io.File

class MainActivity : AppCompatActivity(), CameraManager.FileCallback {

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var imageView: ImageView
    private lateinit var btnTakePicture: Button
    private lateinit var confirmLayout: LinearLayout
    private lateinit var btnConfirm: Button
    private lateinit var btnRetake: Button

    private lateinit var cameraManager: CameraManager

    // Store the image file once it is ready.
    private var capturedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainLayout = findViewById(R.id.main)
        mainLayout.removeAllViews()

        setupViews()
        cameraManager = CameraManager(this, this)
    }

    private fun setupViews() {
        imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        mainLayout.addView(imageView)

        btnTakePicture = Button(this).apply {
            text = "Take Picture"
            setOnClickListener {
                cameraManager.startCameraWorkflow()
            }
        }
        mainLayout.addView(btnTakePicture)

        confirmLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
        }

        btnConfirm = Button(this).apply {
            text = "Confirm"
            setOnClickListener {
                Toast.makeText(this@MainActivity, "Picture Confirmed!", Toast.LENGTH_SHORT).show()
                // Send the image file to your API
                capturedImageFile?.let { file ->
                    ApiUploader.uploadImage(file)
                }
            }
        }
        btnRetake = Button(this).apply {
            text = "Retake"
            setOnClickListener {
                confirmLayout.visibility = android.view.View.GONE
                imageView.setImageDrawable(null)
            }
        }
        confirmLayout.addView(btnConfirm)
        confirmLayout.addView(btnRetake)
        mainLayout.addView(confirmLayout)
    }

    /**
     * Called by CameraManager when the image file is ready.
     */
    override fun onImageFileReady(file: File?) {
        capturedImageFile = file
        // Optionally, you could display the file using libraries such as Glide or decode a Bitmap from file.
        imageView.setImageURI(android.net.Uri.fromFile(file))
        confirmLayout.visibility = android.view.View.VISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraManager.handlePermissionResult(requestCode, permissions, grantResults)
    }

    fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera Permission Denied")
            .setMessage("You have permanently denied the camera permission. Please go to settings to allow it.")
            .setPositiveButton("Go to Settings") { _, _ ->
                PermissionHelper.openAppSettings(this)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
