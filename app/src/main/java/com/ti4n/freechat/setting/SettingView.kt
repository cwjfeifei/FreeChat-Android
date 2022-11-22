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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.BuildConfig
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.db.UserBaseInfoDao
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingView(
    navController: NavController,
    userBaseInfoDao: UserBaseInfoDao,
    globeNavController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF5F5F5)
        )
    }
    val sheetstate = rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = sheetstate)
    BottomSheetScaffold(
        sheetContent = {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = stringResource(id = R.string.logout_tip),
                    color = Color(0xFF666666),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(18.dp))
                Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp)
                TextButton(
                    onClick = {
                        scope.launch {
                            EthUtil.deleteWallet(context, userBaseInfoDao)
                            globeNavController.navigate(Route.MainLogin.route) {
                                popUpTo(0)
                            }
                        }
                    },
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color(0xFFFF3B30)
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm_logout),
                        fontSize = 14.sp
                    )
                }
                Divider(color = Color(0xFFF5F5F5), thickness = 8.dp)
                TextButton(
                    onClick = {
                        scope.launch {
                            sheetstate.collapse()
                        }
                    },
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.confirm_logout),
                        fontSize = 14.sp
                    )
                }
            }
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = Color.White,
        sheetPeekHeight = 0.dp,
        sheetElevation = 8.dp,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                ), title = {
                    HomeTitle(R.string.setting)
                }, navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Image(mipmap = R.mipmap.nav_back)
                    }
                }, modifier = Modifier.statusBarsPadding()
            )
        }, scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            SettingItem(R.string.account_security) {
                navController.navigate(Route.AccountSecurity.route)
            }
            Spacer(modifier = Modifier.height(8.dp))
            SettingItem(R.string.language_setting) {
                navController.navigate(Route.LanguageSetting.route)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                SettingItem(R.string.clear_cache) {
                    navController.navigate(Route.ClearCache.route)
                }
                Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
                SettingItem(R.string.version, endContent = {
                    Text(
                        text = BuildConfig.VERSION_NAME,
                        color = Color(0xFF808080),
                        fontSize = 14.sp
                    )
                })
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = {
                    scope.launch {
                        sheetstate.expand()
                    }
                },
                Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White,
                    contentColor = Color(0xFF1A1A1A)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.logout),
                    color = Color(0xFF1A1A1A),
                    fontSize = 14.sp
                )
            }
        }
    }

}

@Composable
fun SettingItem(
    @StringRes title: Int,
    endContent: @Composable () -> Unit = {
        Image(mipmap = R.mipmap.right_arrow)
    },
    click: () -> Unit = {}
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(Color.White)
        .clickable { click() }
        .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = title), color = Color(0xFF1A1A1A), fontSize = 14.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        endContent()
    }
}