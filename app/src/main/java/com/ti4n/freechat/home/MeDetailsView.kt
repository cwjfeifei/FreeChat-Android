package com.ti4n.freechat.home

import android.os.Build.VERSION.SDK_INT
import android.text.TextUtils
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.login.ProfileInfoItem
import com.ti4n.freechat.util.AnimatedPngDecoder
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.util.IM.DEFAULT_FACEURL
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.launch

@Composable
fun MeDetailsView(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val me by IM.currentUserInfo.collectAsState()
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
            add(AnimatedPngDecoder.Factory())
        }
        .build()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent
        )
        systemUiController.setNavigationBarColor(Color(0xFFF0F0F0))
    }
    // TODO add back arrow
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)),
    ) {
        androidx.compose.foundation.Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(data = if (TextUtils.isEmpty(me.faceURL)) DEFAULT_FACEURL else me.faceURL,)
                    .build(),
                imageLoader = imageLoader
            ),
            contentDescription = null,
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            contentScale = ContentScale.FillBounds
        )
        LazyColumn(modifier = Modifier.background(Color.White)) {
            me?.let {
                item {
                    ProfileInfoItem(faceURL = it.faceURL, nickname = it.nickname, userID = it.userID, gender = me.gender) {
                    }
                    MeDividerItem()
                }
            }
            items(meDetailItems) {
                MeDetailItem(title = it.title) {
                    if (it.route != "") {
                        navController.navigate(it.route)
                    }
                }
                MeDividerItem()
            }
        }
    }
}

@Composable
fun MeDetailItem(title: String, click: () -> Unit = {}) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(Color.White)
        .clickable { click() }
        .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title, color = Color(0xFF1A1A1A), fontSize = 14.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(mipmap = R.mipmap.right_arrow)
    }
}

data class MeDetailItemData(
    val title: String, val route: String = ""
)

val meDetailItems = listOf(
    MeDetailItemData( "备注"),
)