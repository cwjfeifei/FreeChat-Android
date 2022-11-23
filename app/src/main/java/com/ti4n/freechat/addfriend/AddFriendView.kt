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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
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
import com.ti4n.freechat.toast
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.MiddleEllipsisText
import com.ti4n.freechat.widget.SearchView
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    val clipboardManager = LocalClipboardManager.current
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
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF0F0F0)
                ), title = {
                    HomeTitle(R.string.add_friend)
                }, modifier = Modifier.statusBarsPadding(), navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Image(mipmap = R.mipmap.nav_back)
                    }
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
            hintText = stringResource(id = R.string.input_fcid),
            onSearchClick = {
                scope.launch {
                    onSearchFriend(context = context, searchText.value, navController)
                }
            })
        Spacer(modifier = Modifier.height(8.dp))
        if (!showSearchView.value) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FCID",
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(36.dp)
                        .background(
                            Color(0xFFEBEBEB),
                            RoundedCornerShape(4.dp)
                        )
                        .clickable {
                            clipboardManager.setText(AnnotatedString(meInfo?.userID ?: ""))
                            toast.tryEmit(R.string.info_copied)
                        }
                        .padding(horizontal = 12.dp)
                ) {
                    MiddleEllipsisText(
                        text = meInfo?.userID ?: "",
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Image(mipmap = R.mipmap.copy_nor)
                }
            }

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

suspend fun onSearchFriend(context: Context, searchText: String, navController: NavController) {
    if (searchText.isEmpty()) {
        Toast.makeText(context, R.string.search_uid_invalid, Toast.LENGTH_SHORT).show()
    } else {
        val uID = searchText.trim()
        val userInfo = IM.getUserInfo(uID)
        if (userInfo == null) {
            Toast.makeText(context, R.string.user_not_exist, Toast.LENGTH_SHORT).show()
        } else {
            navController.navigate(Route.Profile.jump(uID))
        }
    }

}
