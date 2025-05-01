package com.example.gai

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class ReviewImageActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var btnConfirm: Button
    private lateinit var btnDelete: Button
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val frameLayout = FrameLayout(this)

        // Fullscreen ImageView as background
        imageView = ImageView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        // Buttons container (overlay)
        val buttonLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            ).apply {
                bottomMargin = 60
            }
            setPadding(32, 32, 32, 32)
        }

        btnConfirm = Button(this).apply {
            text = "Confirm"
            setOnClickListener {
                imagePath?.let {
                    val intent = Intent(this@ReviewImageActivity, LoadingActivity::class.java)
                    intent.putExtra("image_path", it)
                    startActivity(intent)
                    finish()
                }
            }
        }




        btnDelete = Button(this).apply {
            text = "Delete"
            setOnClickListener {
                imagePath?.let { File(it).delete() }
                finish()
            }
        }

        buttonLayout.addView(btnConfirm)
        buttonLayout.addView(btnDelete)

        // Add views in order: background first, then overlay
        frameLayout.addView(imageView)
        frameLayout.addView(buttonLayout)

        setContentView(frameLayout)

        imagePath = intent.getStringExtra("image_path")
        imagePath?.let {
            imageView.setImageURI(Uri.fromFile(File(it)))
        }
    }
}
