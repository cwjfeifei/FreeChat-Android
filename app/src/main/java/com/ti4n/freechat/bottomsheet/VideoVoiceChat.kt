package com.ti4n.freechat.bottomsheet

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun VideoVoiceChat(navController: NavController) {
    Column(Modifier.fillMaxWidth().navigationBarsPadding()) {
        SelectableItem("视频通话") {

        }
        Divider(color = Color(0xFFE5E5E5))
        SelectableItem("语音通话") {

        }
        Divider(color = Color(0xFFE5E5E5), thickness = 8.dp)
        SelectableItem("取消") {
            navController.navigateUp()
        }
    }
}