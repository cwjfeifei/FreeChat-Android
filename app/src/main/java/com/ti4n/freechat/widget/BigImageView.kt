package com.ti4n.freechat.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.model.Url

@androidx.compose.runtime.Composable
fun BigImageView(navController: NavController, image: Url) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Black
        )
    }
    var showToolbar by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickableSingle { showToolbar = !showToolbar }
    ) {
        AnimatedVisibility(
            visible = showToolbar,
            exit = slideOutVertically(),
            enter = slideInVertically()
        ) {
            TopAppBar(
                { Text(text = "") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        Modifier.clickableSingle { navController.navigateUp() })
                },
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = Color.Black.copy(0.2f)
            )
        }
        AsyncImage(
            model = image.url,
            contentDescription = null
        )
    }
}