package com.example.gai

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.platform.LocalContext
import com.example.gai.ui.theme.GAITheme
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GAITheme {
                CameraScreen()
            }
        }
    }
}

@Composable
fun CameraScreen() {
    val imageUri = remember { mutableStateOf<Uri?>(null) } // Stores the captured image URI
    val bitmap = remember { mutableStateOf<Bitmap?>(null) } // Stores the captured bitmap
    val context = LocalContext.current // Correctly get the context

    // Ensure rememberLauncherForActivityResult is used correctly
    val takePictureLauncher: ActivityResultLauncher<Uri> =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                // Convert the URI to a bitmap after the photo is taken
                imageUri.value?.let { uri ->
                    try {
                        bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else {
                Toast.makeText(context, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    // Launch camera intent to capture the image
                    val tempUri: Uri = createImageUri(context)
                    imageUri.value = tempUri
                    takePictureLauncher.launch(tempUri)
                }) {
                    Text(text = "Take Picture")
                }

                Spacer(modifier = Modifier.height(16.dp))

                bitmap.value?.let { capturedBitmap ->
                    Image(
                        bitmap = capturedBitmap.asImageBitmap(),
                        contentDescription = "Captured Image",
                        modifier = Modifier.fillMaxWidth().height(250.dp)
                    )
                }
            }
        }
    )
}

fun createImageUri(context: Context): Uri {
    val file = File(context.cacheDir, "captured_image.jpg")
    return Uri.fromFile(file)
}

@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {
    GAITheme {
        CameraScreen()
    }
}
