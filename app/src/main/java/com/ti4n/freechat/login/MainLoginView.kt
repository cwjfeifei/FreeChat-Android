package com.ti4n.freechat.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.ImageButton

@Composable
fun MainLoginView(navController: NavController) {
    var isAgree by remember {
        mutableStateOf(false)
    }
    var showTip by remember {
        mutableStateOf(false)
    }
    LoginCommonView("欢迎使用FreeChat\n享受自由的世界") {
        Spacer(modifier = Modifier.height(20.dp))
        ImageButton("登录", R.mipmap.login_btn) {
            if (isAgree) {
                navController.navigate(Route.Login.route)
            } else {
                showTip = true
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        ImageButton("创建FreeChat账户", R.mipmap.signin_btn) {
            if (isAgree) {
                navController.navigate(Route.Register1.route)
            } else {
                showTip = true
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isAgree, onCheckedChange = {
                isAgree = !isAgree
                if (isAgree) {
                    showTip = false
                }
            })
            Text(
                text = "我已阅读并同意《FreeChat用户协议》、《隐私政策》、《设备权限使用清单》、《防止滥用政策》和《儿童保护政策》等协议。",
                color = Color(0xFF666666),
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (showTip)
            Snackbar {
                Text(text = "请阅读并同意相关协议才能进行注册或登录。")
            }
    }
}

@Composable
fun LoginCommonView(title: String, content: @Composable ColumnScope.() -> Unit) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
            .systemBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Image(R.mipmap.logo)
        Spacer(modifier = Modifier.height(3.dp))
        Image(R.mipmap.freechat)
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = title,
            color = Color(0xFF333333),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        content()
    }
}

enum class LoginType {
    Login, Register
}