package com.example.myapplication

import android.content.Context
import com.example.myapplication.bridge.BridgeImpl
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

object FlutterInit {

    fun init(context: Context) {
        val routeList = ArrayList<String>()
        routeList.add(Routes.page1)
        routeList.add(Routes.page2)
        routeList.add(Routes.page3)

        routeList.forEach { route ->
            FlutterEngine(context).apply {
                navigationChannel.setInitialRoute(route)
                FlutterEngineCache.getInstance().put(route, this)
                dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
                plugins.add(BridgeImpl())
            }
        }
    }
}