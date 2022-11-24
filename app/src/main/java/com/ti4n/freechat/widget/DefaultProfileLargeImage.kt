package com.ti4n.freechat.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ti4n.freechat.R
import kotlin.math.roundToInt

private val images = listOf(
    R.mipmap.ring01,
    R.mipmap.ring02,
    R.mipmap.ring03,
    R.mipmap.ring04,
    R.mipmap.delta01,
    R.mipmap.delta02,
    R.mipmap.heart01,
    R.mipmap.heart02,
    R.mipmap.heart02,
    R.mipmap.heart04,
    R.mipmap.circle01,
    R.mipmap.circle02,
    R.mipmap.circle03,
    R.mipmap.star01
)

@Composable
fun DefaultProfileLargeImage(isSelf: Boolean, modifier: Modifier = Modifier) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp + WindowInsets.systemBars.asPaddingValues()
        .calculateTopPadding()
    val screenWidth = configuration.screenWidthDp
    val maxHeight = screenHeight - 100.dp - (if (isSelf) 0.dp else 42.dp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight)
            .background(Color(0xFFD3E8FE))
    ) {
        images.forEach {
            Image(
                mipmap = it,
                modifier = Modifier.offset(
                    Dp((0..screenWidth.dp.value.roundToInt()).random().toFloat()),
                    Dp((0..maxHeight.value.roundToInt()).random().toFloat())
                )
            )
        }
        Image(mipmap = R.mipmap.logo_profile, Modifier.align(Alignment.Center))
    }
}