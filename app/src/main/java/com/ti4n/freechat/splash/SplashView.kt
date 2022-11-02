package com.ti4n.freechat.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@Composable
fun SplashView(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
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
        val agree = context.dataStore.data.map { it[booleanPreferencesKey("agreePermission")] }
            .firstOrNull() ?: false
        if (agree) {
            val isLogin =
                !context.dataStore.data.map { it[stringPreferencesKey("email")] }.firstOrNull()
                    .isNullOrEmpty()
            delay(1.seconds)
            if (isLogin) {
                navController.navigate(Route.Home.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                }
            } else {
                navController.navigate(Route.MainLogin.route) {
                    popUpTo(Route.Splash.route) { inclusive = true }
                }
            }
        } else {
            navController.navigate(Route.PermissionIntro.route) {
                popUpTo(Route.Splash.route) { inclusive = true }
            }
        }
    }
}