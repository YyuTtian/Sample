package com.example.myapplication

import android.content.Context
import androidx.multidex.MultiDexApplication

class App : MultiDexApplication() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        FlutterInit.init(this)
    }
}