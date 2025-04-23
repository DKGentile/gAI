package com.example.gai

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CameraManager(
    private val activity: Activity,
    private val callback: FileCallback
) {

    interface FileCallback {
        fun onImageFileReady(file: File?)
    }

    private val CAMERA_PERMISSION_CODE = 1001
    private var imageCapture: ImageCapture? = null
    private var preview: Preview? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private var cameraControl: CameraControl? = null
    private var torchEnabled = false

    fun startCameraPreview(lifecycleOwner: LifecycleOwner, previewView: PreviewView) {
        if (!isCameraPermissionGranted()) {
            requestCameraPermission()
            return
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                val camera = cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                cameraControl = camera?.cameraControl
            } catch (exc: Exception) {
                Toast.makeText(activity, "Camera init failed: ${exc.message}", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(activity))
    }

    fun takePicture() {
        if (!isCameraPermissionGranted()) {
            requestCameraPermission()
            return
        }

        val outputFile = createImageFile() ?: return

        val outputOptions = ImageCapture.OutputFileOptions.Builder(outputFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(activity),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    callback.onImageFileReady(outputFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(activity, "Image capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    fun toggleFlash() {
        torchEnabled = !torchEnabled
        cameraControl?.enableTorch(torchEnabled)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir: File = activity.cacheDir
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
        )
    }

    fun handlePermissionResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Camera permission granted", Toast.LENGTH_SHORT).show()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                    Toast.makeText(activity, "Camera permission denied", Toast.LENGTH_SHORT).show()
                } else {
                    if (activity is MainActivity) {
                        activity.showPermissionDeniedDialog()
                    }
                }
            }
        }
    }
}
