package com.ti4n.freechat.splash

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Composable
fun PermissionView(navController: NavController, exitApp: () -> Unit) {

    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    SideEffect {
        systemUiController.setSystemBarsColor(Color.White)
    }
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
            item {
                TitleText(title = R.string.location_permission)
            }
            item {
                ContentText(content = R.string.location_permission_detail)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { exitApp() },
                border = BorderStroke(2.dp, Color(0xFFED5B56)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White, contentColor = Color(0xFFED5B56)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.reject),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            TextButton(
                onClick = {
                    scope.launch {
                        context.dataStore.edit {
                            it[booleanPreferencesKey("agreePermission")] = true
                        }
                        val isLogin =
                            !context.dataStore.data.map { it[stringPreferencesKey("account")] }
                                .firstOrNull().isNullOrEmpty()
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
                },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.agree),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
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
        text = stringResource(id = content), fontSize = 12.sp, color = Color(0xFF1A1A1A)
    )
}