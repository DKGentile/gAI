package com.example.gai

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class now creates a temporary file so that the camera output is cached on disk.
 */
class CameraManager(
    private val activity: Activity,
    private val callback: FileCallback
) {

    interface FileCallback {
        fun onImageFileReady(file: File?)
    }

    private val CAMERA_PERMISSION_CODE = 1001
    private var imageFile: File? = null

    // Launcher for the camera activity using Activity Result API.
    private val cameraLauncher: ActivityResultLauncher<Intent> =
        (activity as? MainActivity)?.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // When using EXTRA_OUTPUT, data may be null
            if (result.resultCode == Activity.RESULT_OK) {
                // Now imageFile should contain the photo path.
                callback.onImageFileReady(imageFile)
            }
        } ?: throw IllegalStateException("Activity must be an instance of MainActivity")

    /**
     * Starts the camera workflow: checks permissions and opens the camera with a file URI.
     */
    fun startCameraWorkflow() {
        if (isCameraPermissionGranted()) {
            launchCameraWithFile()
        } else {
            requestCameraPermission()
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun launchCameraWithFile() {
        // Create a temporary image file
        imageFile = createImageFile()
        imageFile?.let { file ->
            // Use FileProvider to generate a content URI for the file.
            val fileUri: Uri = FileProvider.getUriForFile(
                activity,
                "${activity.packageName}.fileprovider",
                file
            )
            // Prepare the intent and assign the file URI.
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            }
            cameraLauncher.launch(cameraIntent)
        }
    }

    /**
     * Creates a temporary image file in the app's cache directory.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name with a timestamp
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        // For caching, consider using the cache directory.
        val storageDir: File = activity.cacheDir
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg",        /* suffix */
            storageDir     /* directory */
        )
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
        )
    }

    /**
     * Should be called from the Activity’s onRequestPermissionsResult callback.
     */
    fun handlePermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCameraWithFile()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                    // Inform the user permission is necessary.
                    android.widget.Toast.makeText(activity, "Camera permission denied", android.widget.Toast.LENGTH_SHORT).show()
                } else {
                    if (activity is MainActivity) {
                        activity.showPermissionDeniedDialog()
                    }
                }
            }
        }
    }
}
