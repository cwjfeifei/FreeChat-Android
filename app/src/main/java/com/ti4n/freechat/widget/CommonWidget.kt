package com.ti4n.freechat.widget

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
fun ImageButton(
    @StringRes title: Int,
    @DrawableRes mipmap: Int,
    modifier: Modifier = Modifier,
    textColor: Color = Color(0xFF4B6AF7),
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.clickable { onClick() }) {
        Image(mipmap = mipmap)
        Text(
            text = stringResource(id = title),
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}