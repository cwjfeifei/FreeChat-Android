package com.ti4n.freechat.setting

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun ClearCacheView(navController: NavController) {
    val scope = rememberCoroutineScope()
    var cacheSize by remember {
        mutableStateOf(0L)
    }
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            cacheSize = context.cacheDir.getFolderSize()
        }
    }
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(Color.White)
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        TopAppBar(title = {
            Text(
                text = stringResource(id = R.string.clear_cache),
                color = Color(0xFF1A1A1A),
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
            backgroundColor = Color.White,
            elevation = 0.dp,
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
                }
            })
        Spacer(modifier = Modifier.weight(1f))
        Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.align(CenterHorizontally)) {
            Text(
                text = stringResource(id = R.string.cache),
                fontSize = 12.sp,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = cacheSize.spaceSize,
                fontSize = 20.sp,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.cache_tip),
            fontSize = 12.sp,
            color = Color(0xFF666666),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        TextButton(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    context.cacheDir.listFiles()?.forEach {
                        Log.e("TAG", "ClearCacheView: ${it.name}  ${it.deleteRecursively()}")
                    }
                    cacheSize = context.cacheDir.totalSpace / 8 / 1024
                }
            },
            Modifier
                .height(42.dp)
                .fillMaxWidth(), shape = RoundedCornerShape(2.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF3879FD),
                contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.clear_cache),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

val Long.spaceSize: String
    get() {
        var size = this
        if (size < 1024) {
            return "${size}B";
        } else {
            size /= 1024
        }
        if (size < 1024) {
            return "${size}KB";
        } else {
            size /= 1024
        }
        if (size < 1024) {
            size *= 100
            return "${size / 100}.${size % 100}MB"
        } else {
            size = size * 100 / 1024;
            return "${size / 100}.${size % 100}GB"
        }
    }

fun File.getFolderSize(): Long {
    var size: Long = 0
    if (isDirectory) {
        listFiles()?.forEach {
            size += it.getFolderSize()
        }
    } else {
        size = length()
    }
    return size
}
