package com.ti4n.freechat.bottomsheet

import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ti4n.freechat.login.RegisterViewModel


@Composable
fun ChooseImageSource(navController: NavController, viewModel: RegisterViewModel) {
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        viewModel.faceURL.value = it.toString()
        navController.navigateUp()
    }
    Column(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        SelectableItem("拍摄") {}
        Divider(color = Color(0xFFE5E5E5))
        SelectableItem("图片库") {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .clickableSingle { click() }) {
        Text(text = title, color = Color(0xFF1A1A1A), fontSize = 16.sp)
    }
}