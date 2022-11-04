package com.ti4n.freechat.login

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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.address
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.ImageButton
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.kethereum.bip39.model.MnemonicWords

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
        Row(Modifier.fillMaxWidth()) {
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
    @StringRes title: Int,
    @StringRes tip: Int? = null,
    @StringRes back: Int = R.string.back,
    @StringRes next: Int = R.string.next,
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
            .padding(horizontal = 24.dp)
            .systemBarsPadding()
    ) {
        Image(
            mipmap = R.mipmap.nav_back,
            modifier = Modifier
                .clickable { backClick() }
                .padding(vertical = 16.dp)
                .align(Alignment.Start)
        )
        Image(R.mipmap.logo)
        Spacer(modifier = Modifier.height(3.dp))
        Image(R.mipmap.register_freechat)
        Spacer(modifier = Modifier.height(44.dp))
        Text(
            text = stringResource(id = title),
            color = Color(0xFF1A1A1A),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        content()
        Spacer(Modifier.weight(1f))
        tip?.let {
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = 0.dp,
                border = BorderStroke(1.dp, Color(0xFFE6E6E6))
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
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { backClick() },
                border = BorderStroke(2.dp, Color(0xFF3879FD)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White, contentColor = Color(0xFF3879FD)
                )
            ) {
                Text(
                    text = stringResource(id = back),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            TextButton(
                onClick = { nextClick() },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                )
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