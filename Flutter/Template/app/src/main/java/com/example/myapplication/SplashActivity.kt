package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.bridge.BridgeImpl
import com.example.myapplication.databinding.ActivitySplashBinding
import io.flutter.embedding.android.FlutterActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvPage1.setOnClickListener {
            startActivity(FlutterActivity.withCachedEngine(Routes.page1).build(this))
        }

        binding.tvPage2.setOnClickListener {
            startActivity(FlutterActivity.withCachedEngine(Routes.page2).build(this))
        }

        binding.tvPage3.setOnClickListener {
            startActivity(FlutterActivity.withCachedEngine(Routes.page3).build(this))
            nativeCallFlutter()
        }
    }


    private fun nativeCallFlutter() {
        BridgeImpl.nativeCallFlutter.loadFlutterData(1) {
            if (it.isSuccess) {
                it.onSuccess { data ->
                    Log.i("flutter", "客户端从Flutter拿到的值 id=${data.id} name=${data.name}");
                }
            } else {
                it.onFailure { e ->
                    Log.i("flutter", "fail ${e.message} $e");
                }
            }
        }
    }
}