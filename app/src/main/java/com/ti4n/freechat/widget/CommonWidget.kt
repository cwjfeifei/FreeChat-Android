package com.ti4n.freechat.widget

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun Image(
    @DrawableRes mipmap: Int,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    androidx.compose.foundation.Image(
        painter = painterResource(id = mipmap),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale
    )
}

@Composable
fun ImageButton(title: String, @DrawableRes mipmap: Int, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.clickable { onClick() }) {
        Image(mipmap = mipmap)
        Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}