package com.ti4n.freechat.wallet

import android.icu.text.SimpleDateFormat
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.db.RecentTransfer
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.widget.CustomPaddingTextField
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendMoneyView(navController: NavController, viewModel: SendMoneyViewModel = hiltViewModel()) {
    val address by viewModel.toAddress.collectAsState()
    val recentAddress = viewModel.recentAddress.collectAsLazyPagingItems()
    val systemUiController = rememberSystemUiController()
    val barcodeLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {

        } else {
            viewModel.setAddress(result.contents)
        }
    }
    val clipboardManager = LocalClipboardManager.current

    SideEffect {
        systemUiController.setStatusBarColor(color = Color.Transparent)
        systemUiController.setNavigationBarColor(
            color = Color.White
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFCADFFC), Color(0xFFD2EFFF),
                        Color(0xFFFFFFFF)
                    )
                )
            )
            .systemBarsPadding()
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            ), title = {
                HomeTitle(R.string.transfer)
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
                }
            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(Modifier.background(Color.White)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.receive_account),
                            color = Color(0xFF333333),
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            mipmap = R.mipmap.scan_black,
                            modifier = Modifier.clickableSingle {
                                barcodeLauncher.launch(
                                    ScanOptions().setDesiredBarcodeFormats(
                                        ScanOptions.QR_CODE
                                    ).setOrientationLocked(false)
                                )
                            })
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        CustomPaddingTextField(
                            value = address,
                            onValueChange = {
                                viewModel.setAddress(it)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                backgroundColor = Color(0xFFF5F5F5),
                                textColor = Color(0xFF1A1A1A)
                            ),
                            shape = RoundedCornerShape(4.dp),
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.receive_account_hint),
                                    color = Color(0xFFB3B3B3),
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    fontSize = 16.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                            padding = PaddingValues(vertical = 12.dp, horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .height(46.dp)
                                .width(68.dp)
                                .background(Color(0xFF141B33), RoundedCornerShape(4.dp))
                                .clickableSingle {
                                    clipboardManager
                                        .getText()
                                        ?.let {
                                            viewModel.setAddress(it.text)
                                        }
                                }, contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.paste),
                                color = Color.White,
                                style = TextStyle(textAlign = TextAlign.Center)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.recent_transfer),
            color = Color(0xFF4B6AF7),
            modifier = Modifier.padding(16.dp)
        )
        Divider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.1f))
        if (recentAddress.itemCount == 0) {
            Spacer(modifier = Modifier.weight(1f))
            Image(mipmap = R.mipmap.no_data, Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.no_data),
                color = Color(0xFF999999),
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(Modifier.padding(start = 16.dp)) {
                items(recentAddress) {
                    it?.let {
                        ItemRecentTransfer(recentTransfer = it)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            TextButton(
                onClick = { navController.navigateUp() },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.back),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            TextButton(
                onClick = {
                    if (EthUtil.addressExist(address)) {
                        viewModel.addressDone()
                        navController.navigate(Route.SendMoneyInputDetail.route)
                    }
                },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.next),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ItemRecentTransfer(recentTransfer: RecentTransfer) {
    Column {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = recentTransfer.toAddress,
            color = Color(0xFF4D4D4D)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = SimpleDateFormat("yyyy-MM-dd hh:mm").format(recentTransfer.date),
            color = Color(0xFF999999),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Divider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.1f))
    }
}