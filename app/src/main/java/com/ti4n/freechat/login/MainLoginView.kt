package com.ti4n.freechat.login

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Checkbox
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
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
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(Color.White)
    }
    var isAgree by remember {
        mutableStateOf(false)
    }
    var showTip by remember {
        mutableStateOf(false)
    }
    val uriHandler = LocalUriHandler.current
    LoginCommonView(R.string.welcome) {
        Spacer(modifier = Modifier.height(20.dp))
        ImageButton(R.string.login, R.mipmap.login_btn, textColor = Color.White) {
            if (isAgree) {
                navController.navigate(Route.Login.route)
            } else {
                showTip = true
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        ImageButton(R.string.create_account, R.mipmap.signin_btn) {
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
            val text = buildAnnotatedString {
                append(stringResource(id = R.string.agree_app_terms))

                pushStringAnnotation(
                    "user term",
                    "https://docs.freechat.world/yong-hu-yu-fa-lv-tiao-kuan/freechat-yong-hu-xie-yi"
                )
                withStyle(SpanStyle(color = Color(0xFF4B6AF7))) {
                    append(stringResource(id = R.string.user_term))
                }
                pop()
                append("、")
                pushStringAnnotation(
                    "privacy",
                    "https://docs.freechat.world/yong-hu-yu-fa-lv-tiao-kuan/yin-si-zheng-ce"
                )
                withStyle(SpanStyle(color = Color(0xFF4B6AF7))) {
                    append(stringResource(id = R.string.privacy))
                }
                pop()
                append("、")
                pushStringAnnotation(
                    "permission list",
                    "https://docs.freechat.world/yong-hu-yu-fa-lv-tiao-kuan/she-bei-quan-xian-shi-yong-qing-dan"
                )
                withStyle(SpanStyle(color = Color(0xFF4B6AF7))) {
                    append(stringResource(id = R.string.permission_list))
                }
                pop()
                append("、")
                pushStringAnnotation(
                    "avoid abuse",
                    "https://docs.freechat.world/yong-hu-yu-fa-lv-tiao-kuan/fang-zhi-lan-yong-zheng-ce"
                )
                withStyle(SpanStyle(color = Color(0xFF4B6AF7))) {
                    append(stringResource(id = R.string.avoid_abuse))
                }
                pop()

                append(stringResource(id = R.string.and))

                pushStringAnnotation(
                    "child protect",
                    "https://docs.freechat.world/yong-hu-yu-fa-lv-tiao-kuan/er-tong-bao-hu-xie-yi"
                )
                withStyle(SpanStyle(color = Color(0xFF4B6AF7))) {
                    append(stringResource(id = R.string.child_protect))
                }
                pop()

                append(stringResource(id = R.string.etc_term))
            }
            ClickableText(text = text, onClick = {
                text.getStringAnnotations(it, it).firstOrNull()?.let {
                    uriHandler.openUri(it.item)
                }
            })
            Text(
                text = stringResource(id = R.string.agree_app_terms),
                color = Color(0xFF666666),
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (showTip)
            Snackbar {
                Text(text = stringResource(id = R.string.agree_app_terms_tip))
            }
    }
}

@Composable
fun LoginCommonView(@StringRes title: Int, content: @Composable ColumnScope.() -> Unit) {
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
        Image(R.mipmap.register_freechat)
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(id = title),
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