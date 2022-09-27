package com.ti4n.freechat.splash

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.ti4n.freechat.widget.ImageButton
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun PermissionView(navController: NavController) {

    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SideEffect {
        systemUiController.setSystemBarsColor(Color.White)
    }
    Column(Modifier.fillMaxWidth().systemBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(id = R.string.permission_intro),
            fontSize = 17.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Divider(thickness = 1.dp, color = Color(0xFFE6E6E6))
        LazyColumn(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                ContentText(content = R.string.permission_intro_detail)
            }
            item {
                TitleText(title = R.string.file_permission)
            }
            item {
                ContentText(content = R.string.file_permission_detail)
            }
            item {
                TitleText(title = R.string.camera_permission)
            }
            item {
                ContentText(content = R.string.camera_permission_detail)
            }
            item {
                TitleText(title = R.string.contact_permission)
            }
            item {
                ContentText(content = R.string.contact_permission_detail)
            }
            item {
                TitleText(title = R.string.data_permission)
            }
            item {
                ContentText(content = R.string.data_permission_detail)
            }
            item {
                TitleText(title = R.string.vpn_permission)
            }
            item {
                ContentText(content = R.string.vpn_permission_detail)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        ImageButton(title = R.string.i_know, mipmap = R.mipmap.login_btn, textColor = Color.White) {
            scope.launch {
                context.dataStore.edit {
                    it[booleanPreferencesKey("agreePermission")] = true
                }
                val isLogin =
                    !context.dataStore.data.map { it[stringPreferencesKey("account")] }
                        .firstOrNull()
                        .isNullOrEmpty()
                if (isLogin) {
                    navController.navigate(Route.Home.route) {
                        popUpTo(Route.PermissionIntro.route) { inclusive = true }
                    }
                } else {
                    navController.navigate(Route.MainLogin.route) {
                        popUpTo(Route.PermissionIntro.route) { inclusive = true }
                    }
                }
            }
        }
    }
}

@Composable
fun TitleText(@StringRes title: Int) {
    Text(
        text = stringResource(id = title),
        fontSize = 12.sp,
        color = Color(0xFF1A1A1A),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 10.dp)
    )
}

@Composable
fun ContentText(@StringRes content: Int) {
    Text(
        text = stringResource(id = content),
        fontSize = 12.sp,
        color = Color(0xFF1A1A1A)
    )
}