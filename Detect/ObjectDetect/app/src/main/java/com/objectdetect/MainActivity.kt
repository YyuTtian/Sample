package com.objectdetect

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.camera.CameraView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.objectdetect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTransparentStatusBar()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), 1)
        }

        binding.camera.addCallback(object : CameraView.Callback() {

            override fun onBitmapChange(cameraView: CameraView?, bitmap: Bitmap?) {
                super.onBitmapChange(cameraView, bitmap)
                check(bitmap)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        binding.camera.start()
    }

    override fun onPause() {
        super.onPause()
        binding.camera.stop()
    }

    private var preCheckTs = 0L
    private fun check(bitmap: Bitmap?) {
        if (bitmap == null || System.currentTimeMillis() - preCheckTs < 200) {
            return
        }
        preCheckTs = System.currentTimeMillis()

        val options = ObjectDetectorOptions.Builder()
            .setDetectorMode(ObjectDetectorOptions.SINGLE_IMAGE_MODE) // 处理单张图片
            .build()

        val objectDetection = ObjectDetection.getClient(options)

        val image = InputImage.fromBitmap(bitmap, 0)

        objectDetection.process(image).addOnSuccessListener {
            for (i in it) {
                binding.tipView.setRect(i.boundingBox)
            }
        }.addOnFailureListener {
            println("check err " + it.message)
        }
    }

    private fun setTransparentStatusBar() {
        try {
            val window = this.window
            val decorView = window.decorView
            val option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            decorView.systemUiVisibility = option
            window.statusBarColor = Color.TRANSPARENT
        } catch (e: Exception) {
        }
    }
}