package com.ti4n.freechat.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.ImageButton

@Composable
fun Register1View(navController: NavController, viewModel: RegisterViewModel = hiltViewModel()) {
    val scrollState = rememberScrollState()
    val words by viewModel.words.collectAsState()
    LoginCommonView(R.string.keep_mnemonic_to_find_account) {
        Spacer(Modifier.height(14.dp))
        Column(Modifier.verticalScroll(scrollState)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                userScrollEnabled = false,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            ) {
                items(words) {
                    Card(
                        backgroundColor = Color(0xFFF4F6FA),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp),
                        elevation = 0.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = it,
                                color = Color(0xFF4D4D4D),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = { },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6B8FF8))
                ) {
                    Text(
                        text = "保存为图片",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                TextButton(
                    onClick = { },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6B8FF8))
                ) {
                    Text(
                        text = "发送为短信",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                TextButton(
                    onClick = { },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6B8FF8))
                ) {
                    Text(
                        text = "备份为keystore",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = 4.dp
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "注意", color = Color(0xFF4D4D4D), fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "FreeChat为去中心化的社交应用，你的安全措施由助记词进行保护，如果助记词遗失或泄漏，您的账户及财产将会失去保护，请妥善保管。\n",
                        color = Color(0xFF666666),
                        fontSize = 10.sp
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp), Arrangement.SpaceBetween
        ) {
            ImageButton(title = R.string.back, mipmap = R.mipmap.return_btn) {
                navController.navigateUp()
            }
            ImageButton(
                title = R.string.next,
                mipmap = R.mipmap.next_btn,
                textColor = Color.White
            ) {
                navController.navigate(Route.Register2.route)
            }
        }
    }
}