package com.ti4n.freechat.wallet

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment.DIRECTORY_DCIM
import android.os.Environment.DIRECTORY_PICTURES
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns.RELATIVE_PATH
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.ti4n.freechat.R
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.widget.HomeTitle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.OutputStream

@Composable
fun ReceiveMoneyView(navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var address by remember {
        mutableStateOf("")
    }
    LaunchedEffect(Unit) {
        context.dataStore.data.map { it[stringPreferencesKey("address")] }.filterNotNull()
            .collectLatest {
                address = it
            }
    }
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(backgroundColor = Color.Transparent, title = {
            HomeTitle(R.string.receive_money)
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                com.ti4n.freechat.widget.Image(mipmap = R.mipmap.nav_back)
            }
        }, elevation = 0.dp)
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = stringResource(id = R.string.scan_qrcode_to_send_me), color = Color(0xFF666666))
        Spacer(modifier = Modifier.height(20.dp))
        if (address.isNotEmpty()) {
            val qrcode = BarcodeEncoder().encodeBitmap(address, BarcodeFormat.QR_CODE, 600, 600)
            Image(
                bitmap = qrcode.asImageBitmap(),
                contentDescription = "",
                modifier = Modifier
                    .size(200.dp)
                    .background(Color(0xFFF5F5F5))
                    .padding(6.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "账户名", color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = address, color = Color(0xFF333333))
                Spacer(modifier = Modifier.width(4.dp))
                com.ti4n.freechat.widget.Image(mipmap = R.mipmap.copy_nor)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                TextButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val cv = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, address)
                                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    put(RELATIVE_PATH, DIRECTORY_PICTURES)
                                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                                }
                            }
                            val fos: OutputStream?
                            context.contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                cv
                            )?.let {
                                fos = context.contentResolver.openOutputStream(it)
                                fos?.use { qrcode.compress(Bitmap.CompressFormat.PNG, 100, it) }
                                fos?.flush()
                                fos?.close()
                                cv.clear()
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    cv.put(MediaStore.MediaColumns.IS_PENDING, 0)
                                }
                                context.contentResolver.update(it, cv, null, null)
                            }
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF799EF9), Color(0xFF425FF7)
                                )
                            ), RoundedCornerShape(4.dp)
                        )
                ) {
                    com.ti4n.freechat.widget.Image(mipmap = R.mipmap.save)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = stringResource(id = R.string.save_qrcode), color = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, address)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, null)
                        context.startActivity(shareIntent)
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFFFF726D), Color(0xFFF54B64)
                                )
                            ), RoundedCornerShape(4.dp)
                        )
                ) {
                    com.ti4n.freechat.widget.Image(mipmap = R.mipmap.share)
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(text = stringResource(id = R.string.share), color = Color.White)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.attention),
                color = Color(0xFF4D4D4D),
                modifier = Modifier.align(CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.attention_content),
                color = Color(0xFF666666),
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}