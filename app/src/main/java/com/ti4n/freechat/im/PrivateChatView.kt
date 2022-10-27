package com.ti4n.freechat.im

import android.app.Activity
import android.opengl.GLSurfaceView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.util.coloredShadow
import com.ti4n.freechat.widget.Image

@Composable
fun PrivateChatView(toUserId: String, navController: NavController) {
    var inputType by remember {
        mutableStateOf(InputType.Text)
    }
    val activity = LocalContext.current as Activity
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent
        )
        systemUiController.setNavigationBarColor(Color(0xFFF0F0F0))
    }
    Column(Modifier.background(Color(0xFFF0F0F0))) {
//        TopAppBar()
        Spacer(modifier = Modifier.weight(1f))
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .coloredShadow(blurRadius = 8.dp, color = Color(0xFFCCCCCC), offsetY = (-2).dp)
                .background(Color(0xFFF0F0F0))
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (inputType) {
                InputType.Voice -> Image(mipmap = R.mipmap.jianpan, modifier = Modifier.clickable {
                    inputType = InputType.Text
                })

                else -> Image(mipmap = R.mipmap.yuyin_chat, modifier = Modifier.clickable {
                    inputType = InputType.Voice
                })
            }
            Spacer(modifier = Modifier.weight(1f))
            Image(mipmap = R.mipmap.biaoqing, modifier = Modifier.clickable {
                inputType = InputType.Emoji
            })
            Image(mipmap = R.mipmap.more_type, modifier = Modifier.clickable {
                inputType = InputType.More
            })
        }
        AnimatedVisibility(
            visible = inputType == InputType.More,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            ) {
                Divider(color = Color(0xffe6e6e6), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    ItemMoreFunction(image = R.mipmap.camera_chat, text = R.string.camera) {

                    }
                    ItemMoreFunction(image = R.mipmap.picture_chat, text = R.string.picture) {

                    }
                    ItemMoreFunction(image = R.mipmap.transfer_chat, text = R.string.transfer) {

                    }
                }
            }
        }
    }
}

enum class InputType {
    Text, Voice, Emoji, More
}

@Composable
fun ItemMoreFunction(@DrawableRes image: Int, @StringRes text: Int, click: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(mipmap = image,
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .size(58.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .clickable { click() })
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = stringResource(id = text), color = Color(0xFF666666), fontSize = 12.sp)
    }
}