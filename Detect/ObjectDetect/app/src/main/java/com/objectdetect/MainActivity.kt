package com.objectdetect

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import com.objectdetect.camera.CameraView
import com.objectdetect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}