package com.example.gai

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import java.io.File

class MainActivity : AppCompatActivity(), CameraManager.FileCallback {

    private lateinit var mainLayout: FrameLayout
    private lateinit var previewView: PreviewView
    private lateinit var imageView: ImageView
    private lateinit var btnTakePicture: Button
    private lateinit var btnConfirm: Button
    private lateinit var btnRetake: Button
    private lateinit var btnToggleFlash: ImageButton // Added for the flashlight toggle

    private lateinit var cameraManager: CameraManager

    private var capturedImageFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create root layout programmatically
        mainLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        setContentView(mainLayout)

        // Set up camera preview
        previewView = PreviewView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        mainLayout.addView(previewView)

        setupViews()

        // Start camera preview
        cameraManager = CameraManager(this, this)
        cameraManager.startCameraPreview(this, previewView)
    }

    private fun setupViews() {
        val verticalLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
            ).apply {
                bottomMargin = 60
            }
            gravity = Gravity.CENTER_HORIZONTAL
        }

        imageView = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                600
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        verticalLayout.addView(imageView)

        val btnTakePicture = Button(this).apply {
            text = ""
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.WHITE)
            }
            // Set width and height to make it a circle
            val size = 200
            layoutParams = LinearLayout.LayoutParams(size, size).apply {
                gravity = Gravity.CENTER // Optional: center it horizontally
                bottomMargin = 50        // Moves it up by 50 pixels from the bottom
            }
            setOnClickListener {
                cameraManager.takePicture()
            }
        }
        verticalLayout.addView(btnTakePicture)

        btnConfirm = Button(this).apply {
            text = "Confirm"
            setOnClickListener {
                /*Toast.makeText(this@MainActivity, "Picture Confirmed!", Toast.LENGTH_SHORT).show()
                capturedImageFile?.let { file ->
                    ApiUploader.uploadImage(file)
                }*/
            }
            visibility = View.GONE
        }

        btnRetake = Button(this).apply {
            text = "Delete"
            setOnClickListener {
                btnConfirm.visibility = View.GONE
                btnRetake.visibility = View.GONE
                imageView.setImageDrawable(null)
            }
            visibility = View.GONE
        }

        verticalLayout.addView(btnConfirm)
        verticalLayout.addView(btnRetake)

        mainLayout.addView(verticalLayout)

        // Flashlight button setup (top-right)
        btnToggleFlash = ImageButton(this).apply {
            setImageResource(android.R.drawable.btn_star_big_on) // Placeholder for the flashlight icon
            layoutParams = FrameLayout.LayoutParams(
                100, // Size of the button (circle)
                100,
                Gravity.TOP or Gravity.END
            ).apply {
                topMargin = 20
                rightMargin = 20
            }
            setBackgroundResource(android.R.color.transparent) // Transparent background for circle effect
            setOnClickListener {
                cameraManager.toggleFlash() // Toggle flashlight on button click
            }
        }
        mainLayout.addView(btnToggleFlash)
    }

    override fun onImageFileReady(file: File?) {
        capturedImageFile = file
        file?.let {
            val intent = Intent(this, ReviewImageActivity::class.java)
            intent.putExtra("image_path", it.absolutePath)
            startActivity(intent)
        }
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
