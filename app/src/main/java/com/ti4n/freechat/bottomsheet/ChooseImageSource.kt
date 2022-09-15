package com.ti4n.freechat.bottomsheet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ChooseImageSource(navController: NavController) {
    Column(Modifier.fillMaxWidth().navigationBarsPadding()) {
        SelectableItem("拍摄") {

        }
        Divider(color = Color(0xFFE5E5E5))
        SelectableItem("图片库") {

        }
        Divider(color = Color(0xFFE5E5E5))
        SelectableItem("NET库") {

        }
        Divider(color = Color(0xFFE5E5E5), thickness = 8.dp)
        SelectableItem("取消") {
            navController.navigateUp()
        }
    }
}

@Composable
fun SelectableItem(title: String, click: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .clickable { click() }
    ) {
        Text(text = title, color = Color(0xFF1A1A1A), fontSize = 16.sp)
    }
}