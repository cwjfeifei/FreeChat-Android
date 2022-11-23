package com.ti4n.freechat.login

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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

@Composable
fun MainLoginView(navController: NavController, appExit: () -> Unit) {
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
    BackHandler {
        appExit()
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
        Spacer(modifier = Modifier.height(44.dp))
        Text(
            text = stringResource(id = R.string.welcome), color = Color(0xFF1A1A1A),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextButton(
            onClick = {
                if (isAgree) {
                    navController.navigate(Route.Login.route)
                } else {
                    showTip = true
                }
            },
            Modifier
                .height(44.dp)
                .fillMaxWidth(), shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF3879FD),
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.login),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedButton(
            onClick = {
                if (isAgree) {
                    navController.navigate(Route.Register1.route)
                } else {
                    showTip = true
                }
            },
            Modifier
                .height(44.dp)
                .fillMaxWidth(), shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color(0xFF3879FD)
            ),
            border = BorderStroke(2.dp, Color(0xFF3879FD))
        ) {
            Text(
                text = stringResource(id = R.string.create_account),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth()) {
            Checkbox(
                checked = isAgree,
                onCheckedChange = {
                    isAgree = !isAgree
                    if (isAgree) {
                        showTip = false
                    }
                },
                colors = CheckboxDefaults.colors(
                    uncheckedColor = Color(0xFF666666),
                    checkmarkColor = Color.White
                )
            )
            val text = buildAnnotatedString {
                append(stringResource(id = R.string.agree_app_terms))

                pushStringAnnotation(
                    "user term",
                    "https://docs.freechat.world/yong-hu-yu-fa-lv-tiao-kuan/freechat-yong-hu-xie-yi"
                )
                withStyle(SpanStyle(color = Color(0xFF3879FD))) {
                    append(stringResource(id = R.string.user_term))
                }
                pop()
                append("、")
                pushStringAnnotation(
                    "privacy",
                    "https://docs.freechat.world/yong-hu-yu-fa-lv-tiao-kuan/yin-si-zheng-ce"
                )
                withStyle(SpanStyle(color = Color(0xFF3879FD))) {
                    append(stringResource(id = R.string.privacy))
                }
                pop()
                append("、")
                pushStringAnnotation(
                    "permission list",
                    "https://docs.freechat.world/yong-hu-yu-fa-lv-tiao-kuan/she-bei-quan-xian-shi-yong-qing-dan"
                )
                withStyle(SpanStyle(color = Color(0xFF3879FD))) {
                    append(stringResource(id = R.string.permission_list))
                }
                pop()
                append("、")
                pushStringAnnotation(
                    "avoid abuse",
                    "https://docs.freechat.world/yong-hu-yu-fa-lv-tiao-kuan/fang-zhi-lan-yong-zheng-ce"
                )
                withStyle(SpanStyle(color = Color(0xFF3879FD))) {
                    append(stringResource(id = R.string.avoid_abuse))
                }
                pop()

                append(stringResource(id = R.string.and))

                pushStringAnnotation(
                    "child protect",
                    "https://docs.freechat.world/yong-hu-yu-fa-lv-tiao-kuan/er-tong-bao-hu-xie-yi"
                )
                withStyle(SpanStyle(color = Color(0xFF3879FD))) {
                    append(stringResource(id = R.string.child_protect))
                }
                pop()

                append(stringResource(id = R.string.etc_term))
            }
            ClickableText(text = text, onClick = {
                text.getStringAnnotations(it, it).firstOrNull()?.let {
                    uriHandler.openUri(it.item)
                }
            }, modifier = Modifier.padding(end = 24.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
        if (showTip)
            Snackbar {
                Text(text = stringResource(id = R.string.agree_app_terms_tip))
            }
    }
}

@Composable
fun LoginCommonView(
    title: String,
    @StringRes tip: Int? = null,
    @StringRes back: Int = R.string.back,
    @StringRes next: Int = R.string.next,
    titleEndContent: @Composable RowScope.() -> Unit = {},
    backClick: () -> Unit,
    nextClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
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
            .systemBarsPadding()
    ) {
        IconButton(onClick = { backClick() }, modifier = Modifier.align(Alignment.Start)) {
            Image(mipmap = R.mipmap.nav_back)
        }
        Image(R.mipmap.logo)
        Spacer(modifier = Modifier.height(3.dp))
        Image(R.mipmap.register_freechat)
        Spacer(modifier = Modifier.height(44.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                color = Color(0xFF1A1A1A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(8.dp))
            titleEndContent()
        }

        Spacer(modifier = Modifier.height(24.dp))
        Column(Modifier.padding(horizontal = 24.dp)) {
            content()
        }
        Spacer(Modifier.weight(1f))
        tip?.let {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                elevation = 0.dp,
                border = BorderStroke(1.dp, Color(0xFFE6E6E6)),
                backgroundColor = Color.White
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.tip),
                        color = Color(0xFF666666),
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = it),
                        color = Color(0xFF666666),
                        fontSize = 10.sp
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp), // adjust bottom bar
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            TextButton(
                onClick = { backClick() },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ),
            ) {
                Text(
                    text = stringResource(id = back),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            TextButton(
                onClick = { nextClick() },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ),
            ) {
                Text(
                    text = stringResource(id = next),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

enum class LoginType {
    Login, Register
}