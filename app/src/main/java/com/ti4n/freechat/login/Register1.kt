package com.ti4n.freechat.login

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun Register1View(
    navController: NavController, viewModel: RegisterViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val words by viewModel.words.collectAsState()
    val view = LocalView.current
    val clipboardManager = LocalClipboardManager.current
    LoginCommonView(
        stringResource(id = R.string.keep_mnemonic_to_find_account),
        backClick = { navController.navigateUp() },
        nextClick = { navController.navigate(Route.Register2.route) },
        tip = R.string.register_tip
    ) {
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
            Row(modifier = Modifier.fillMaxWidth()) {
                val context = LocalContext.current
                TextButton(
                    onClick = {
                        // 将view界面存储到Gallery (TODO 仅保存到当前compose的图)
                        val bitmap = Bitmap.createBitmap(
                            view.width, view.height, Bitmap.Config.ARGB_8888
                        )
                        val canvas = Canvas(bitmap)
                        view.draw(canvas)

                        val fileName = "FCC_" + SimpleDateFormat("yyyy-MM-dd hh:mm").format(
                            Date(System.currentTimeMillis())
                        )
                        // 保存至系统图库
                        MediaStore.Images.Media.insertImage(
                            context.contentResolver, bitmap, fileName, "FreeChat words"
                        )
                        Toast.makeText(context, "图片已保存", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF141B33)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.save_to_gallery),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                TextButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(words.joinToString(" ")))
                        Toast.makeText(context, "已复制到粘贴板", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF141B33)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(id = R.string.copy),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}