package com.d10ng.app.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.d10ng.app.demo.pages.NavGraphs
import com.d10ng.app.demo.ui.DLTheme
import com.d10ng.app.demo.utils.NavControllerUtils
import com.d10ng.app.status.NetworkStatusManager
import com.d10ng.app.view.lockScreenOrientation
import com.d10ng.app.view.setStatusBar
import com.d10ng.compose.model.UiViewModelManager
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 锁定屏幕方向
        lockScreenOrientation()
        // 设置状态栏颜色
        setStatusBar()
        // 启动网络监听
        NetworkStatusManager.start()

        setContent {
            DLTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val engine = rememberAnimatedNavHostEngine()
                    val navController = engine.rememberNavController()
                    LaunchedEffect(navController) {
                        NavControllerUtils.register(this@MainActivity, navController)
                    }

                    DestinationsNavHost(
                        engine = engine,
                        navController = navController,
                        navGraph = NavGraphs.root,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            UiViewModelManager.Init(act = this)
        }
    }
}