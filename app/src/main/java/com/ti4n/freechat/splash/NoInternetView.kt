package com.ti4n.freechat.splash

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
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
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.util.pingFreeChat
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.launch

@Composable
fun NoInternetView(navController: NavController) {
//    val systemUiController = rememberSystemUiController()
//    val scope = rememberCoroutineScope()
//    SideEffect {
//        systemUiController.setSystemBarsColor(Color.White)
//    }
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier
//            .fillMaxSize()
//            .systemBarsPadding()
//            .padding(horizontal = 20.dp)
//    ) {
//        Spacer(modifier = Modifier.weight(1f))
//        Image(mipmap = R.mipmap.no_internet)
//        Spacer(modifier = Modifier.height(20.dp))
//        Text(
//            text = stringResource(id = R.string.no_internet),
//            color = Color(0xFF4D4D4D),
//            fontSize = 12.sp
//        )
//        Spacer(modifier = Modifier.height(8.dp))
//        Text(
//            text = stringResource(id = R.string.use_vpn_to_access),
//            color = Color(0xFF4D4D4D),
//            fontSize = 12.sp
//        )
//        Spacer(modifier = Modifier.weight(1f))
//        Row(
//            Modifier
//                .fillMaxWidth()
//                .padding(bottom = 20.dp)
//                .height(44.dp)
//        ) {
//            OutlinedButton(
//                onClick = {
//                    scope.launch {
//                        Log.e("NoInternet", "NoInternetView: ${pingFreeChat()}")
//                    }
//                },
//                shape = RoundedCornerShape(4.dp),
//                border = BorderStroke(
//                    2.dp,
//                    Color(0xFF4B6AF7)
//                ),
//                colors = ButtonDefaults.outlinedButtonColors(
//                    backgroundColor = Color.White,
//                    contentColor = Color(0xFF4B6AF7)
//                ),
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = stringResource(id = R.string.re_test),
//                    fontSize = 16.sp,
//                    fontWeight = FontWeight.Medium
//                )
//            }
//            Spacer(modifier = Modifier.width(7.dp))
//            ImageButton(
//                title = R.string.use_vpn,
//                mipmap = R.mipmap.next_btn,
//                modifier = Modifier.weight(1f),
//                Color.White
//            ) {
//
//            }
//        }
//    }
}