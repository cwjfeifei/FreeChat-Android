package com.ti4n.freechat.splash

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.login.LoginViewModel
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun SplashView(navController: NavController, viewModel: LoginViewModel = hiltViewModel()) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SideEffect {
        systemUiController.setSystemBarsColor(Color.Transparent)
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(
            mipmap = R.mipmap.splash_bg,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Image(
                mipmap = R.mipmap.splash_freechat
            )
            Spacer(modifier = Modifier.width(7.dp))
            Box(
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .size(12.dp)
                    .background(Color(0xFFFFC43B), CircleShape)
            )
        }
    }
    LaunchedEffect(Unit) {
        launch {
            val launchTime = System.currentTimeMillis()
            viewModel.autoLoginRoute.filter { it.isNotEmpty() }.collectLatest {
                var duration = System.currentTimeMillis() - launchTime
                if (duration < 1000) {
                    delay(1000 - duration)
                }
                navController.navigate(it)
            }
        }

        val agree = context.dataStore.data.map { it[booleanPreferencesKey("agreePermission")] }
            .firstOrNull() ?: false
        if (agree) {
            scope.launch {
                viewModel.autoLogin(context)
            }
        } else {
            navController.navigate(Route.PermissionIntro.route) {
                popUpTo(Route.Splash.route) { inclusive = true }
            }
        }

    }
}