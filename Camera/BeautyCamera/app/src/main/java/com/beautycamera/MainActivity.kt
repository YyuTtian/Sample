package com.beautycamera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.beautycamera.databinding.ActivityMainBinding
import com.camera.QTTBeautyProcessor
import com.camera.camera.CameraManager
import com.camera.framework.modules.consumers.TextureViewConsumer

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

        CameraManager.getInstance().init(this, QTTBeautyProcessor(this))
        CameraManager.getInstance().attachOffScreenConsumer(TextureViewConsumer())
        CameraManager.getInstance().setLocalPreview(binding.texture)
    }

    override fun onResume() {
        super.onResume()
        CameraManager.getInstance().startCapture()
    }

    override fun onPause() {
        super.onPause()
        CameraManager.getInstance().stopCapture()
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