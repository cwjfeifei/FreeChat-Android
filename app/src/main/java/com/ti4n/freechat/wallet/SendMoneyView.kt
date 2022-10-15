package com.ti4n.freechat.wallet

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.swipeable
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.ImageButton


@Composable
fun SendMoneyView(navController: NavController, viewModel: SendMoneyViewModel = hiltViewModel()) {
    val address by viewModel.toAddress.collectAsState()
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
            color = Color(0xFFF0F0F0)
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            mipmap = R.mipmap.transfer_bg,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TopAppBar(backgroundColor = Color.Transparent, title = {
                HomeTitle(R.string.transfer)
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            }, elevation = 0.dp)
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
                                modifier = Modifier.clickable {
                                    barcodeLauncher.launch(
                                        ScanOptions().setDesiredBarcodeFormats(
                                            ScanOptions.QR_CODE
                                        ).setOrientationLocked(false)
                                    )
                                })
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row {
                            TextField(
                                value = address,
                                onValueChange = {
                                    viewModel.setAddress(it)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                colors = TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    backgroundColor = Color(0xFFF5F5F5)
                                ),
                                shape = RoundedCornerShape(4.dp),
                                placeholder = {
                                    Text(
                                        text = stringResource(id = R.string.receive_account_hint),
                                        color = Color(0xFFB3B3B3),
                                        style = TextStyle(textAlign = TextAlign.Center),
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        fontSize = 12.sp
                                    )
                                },
                                textStyle = TextStyle(
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFFB3B3B3)
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    viewModel.addressDone()
                                })
                            )
                            Box(
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(70.dp)
                                    .background(Color(0xFF6B8FF8), RoundedCornerShape(4.dp))
                                    .clickable {
                                        clipboardManager
                                            .getText()
                                            ?.let {
                                                viewModel.setAddress(it.text)
                                            }
                                    }, contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "粘贴",
                                    color = Color.White,
                                    style = TextStyle(textAlign = TextAlign.Center)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .padding(horizontal = 20.dp), Arrangement.SpaceBetween
            ) {
                ImageButton(title = R.string.close, mipmap = R.mipmap.return_btn) {
                    navController.navigateUp()
                }
                ImageButton(
                    title = R.string.next,
                    mipmap = R.mipmap.next_btn,
                    textColor = Color.White
                ) {
                    navController.navigate(Route.SendMoneyInputDetail.route)
                }
            }
        }
    }
}