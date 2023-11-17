package com.example.myapplication.bridge

import FlutterCallNative
import NativeBean
import NativeCallFlutter
import io.flutter.embedding.engine.plugins.FlutterPlugin

class BridgeImpl : FlutterPlugin, FlutterCallNative {

    companion object {
        lateinit var nativeCallFlutter: NativeCallFlutter
    }


    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        FlutterCallNative.setUp(binding.binaryMessenger, this)
        nativeCallFlutter = NativeCallFlutter(binding.binaryMessenger)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        FlutterCallNative.setUp(binding.binaryMessenger, null)
    }


    override fun loadNativeData(id: Long): NativeBean {
        return NativeBean(666, "客户端返回的数据")
    }
}