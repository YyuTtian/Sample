package com.example.myapplication

import android.content.Context
import com.example.myapplication.bridge.BridgeImpl
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor

object FlutterInit {

    fun init(context: Context) {

        /**
         * navigationChannel.setInitialRoute(Routes.page1)
        navigationChannel.setInitialRoute(Routes.page2)
        navigationChannel.setInitialRoute(Routes.page3)
         */

        FlutterEngine(context).apply {
            navigationChannel.setInitialRoute(Routes.page1)
            dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
            FlutterEngineCache.getInstance().put(Routes.page1, this)
        }

        FlutterEngine(context).apply {
            navigationChannel.setInitialRoute(Routes.page2)
            dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
            FlutterEngineCache.getInstance().put(Routes.page2, this)
        }

        FlutterEngine(context).apply {
            navigationChannel.setInitialRoute(Routes.page3)
            dartExecutor.executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault())
            FlutterEngineCache.getInstance().put(Routes.page3, this)
        }

        // 添加通信接口
        FlutterEngine(context).plugins.add(BridgeImpl())
    }
}