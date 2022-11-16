package com.ti4n.freechat.setting

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.BuildConfig
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSecurityView(navController: NavController) {
    val userInfo by IM.currentUserInfo.collectAsState()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF5F5F5)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .systemBarsPadding()
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFFF5F5F5)
            ), title = {
                HomeTitle(R.string.account_security)
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
                }
            })
        SettingItem(R.string.account, endContent = {
            Text(
                text = userInfo?.userID ?: "",
                color = Color(0xFF808080),
                fontSize = 14.sp
            )
        })
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            SettingItem(R.string.backup_mnemonic) {
//                navController.navigate(Route.LanguageSetting.route)
            }
            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
            SettingItem(R.string.change_password) {
                navController.navigate(Route.ChangePassword.route)
            }
            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
            SettingItem(R.string.email, endContent = {
                Text(
                    text = userInfo?.email ?: "",
                    color = Color(0xFF808080),
                    fontSize = 14.sp
                )
            })
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}