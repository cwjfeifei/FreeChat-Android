package com.ti4n.freechat.addfriend

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.model.im.toBaseInfo
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.SearchView
import kotlinx.coroutines.launch

@Composable
fun AddFriendView(navController: NavController) {
    val meInfo by IM.currentUserInfo.collectAsState()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    val showSearchView = remember {
        mutableStateOf(false)
    }
    val searchText = remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val barcodeLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        result.contents?.let {
            showSearchView.component2()(true)
            searchText.component2()(it)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = !showSearchView.value, enter = expandVertically(), exit = shrinkVertically()
        ) {
            TopAppBar(backgroundColor = Color(0xFFF0F0F0), title = {
                HomeTitle(R.string.add_friend)
            }, elevation = 0.dp, modifier = Modifier.statusBarsPadding(), navigationIcon = {
                Image(mipmap = R.mipmap.nav_back,
                    modifier = Modifier
                        .clickable { navController.navigateUp() }
                        .padding(14.dp))
            }, actions = {
                Image(mipmap = R.mipmap.scan_black, modifier = Modifier
                    .clickable {
                        barcodeLauncher.launch(
                            ScanOptions()
                                .setDesiredBarcodeFormats(
                                    ScanOptions.QR_CODE
                                )
                                .setOrientationLocked(false)
                        )
                    }
                    .padding(14.dp))
            })
        }
        Spacer(modifier = Modifier.height(8.dp))
        SearchView(
            showSearchView = showSearchView,
            searchText = searchText,
            hintText = "FCID",
            onSearchClick = {
                scope.launch {
                    onSearchFriend(context = context, searchText.value, navController)
                }
            })
        Spacer(modifier = Modifier.height(8.dp))
        if (!showSearchView.value) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "FCID: ${meInfo?.userID}",
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 14.dp, end = 14.dp),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            val qrcode =
                BarcodeEncoder().encodeBitmap(meInfo?.userID, BarcodeFormat.QR_CODE, 600, 600)
            androidx.compose.foundation.Image(
                bitmap = qrcode.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(180.dp)
                    .background(Color.White)
                    .padding(6.dp)
            )
        } else {
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(top = 8.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                onSearchFriend(context = context, searchText.value, navController)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Image(mipmap = R.mipmap.searchpage_search)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${stringResource(id = R.string.search)}: ",
                        color = Color(0xFF333333),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = searchText.value,
                        color = Color(0xFF57BE6A),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider(thickness = 0.5.dp, color = Color(0xFFEBEBEB), startIndent = 16.dp)
            }
        }
    }
}

suspend fun onSearchFriend(context: Context, searchText : String, navController: NavController) {
    if (searchText.isEmpty()) {
        Toast.makeText(context, R.string.search_uid_invalid, Toast.LENGTH_SHORT).show()
    } else {
//        val uID = "0x903d6a8b2407ccdc388b46f3c79d088db25186ab"
        val uID = searchText.trim()
        val userInfo = IM.getUserInfo(uID)
        if (userInfo == null) {
            Toast.makeText(context, R.string.user_not_exist, Toast.LENGTH_SHORT).show()
        } else {
            navController.navigate(Route.Profile.jump(uID))
        }
    }

}
