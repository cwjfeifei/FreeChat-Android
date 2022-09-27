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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        TopAppBar(backgroundColor = Color(0xFFF0F0F0), title = {
            HomeTitle(R.string.transfer)
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        })
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(
                Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF799EF9),
                            Color(0xFF425FF7),
                        )
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.receive_account),
                            color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp
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
                    TextField(
                        value = address, onValueChange = {
                            viewModel.setAddress(it)
                        }, modifier = Modifier
                            .fillMaxWidth(), colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            backgroundColor = Color(0x2BF5F5F5)
                        ), shape = RoundedCornerShape(4.dp),
                        placeholder = {
                            Text(
                                text = stringResource(id = R.string.receive_account_hint),
                                color = Color.White.copy(0.7f),
                                style = TextStyle(textAlign = TextAlign.Center),
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        textStyle = TextStyle(textAlign = TextAlign.Center, color = Color.White)
                    )
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