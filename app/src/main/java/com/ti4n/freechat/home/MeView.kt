package com.ti4n.freechat.home

import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
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
import com.ti4n.freechat.util.AnimatedPngDecoder
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.launch

@Composable
fun MeView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MeViewModel = hiltViewModel()
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0)),
    ) {
        androidx.compose.foundation.Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(data = "file:///android_asset/face.apng")
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
                Log.e("TAG", "MeView: ${me.userID} ${me.faceURL} ${me.nickname}")
                item {
                    MeInfoItem(avatar = it.faceURL, nickname = it.nickname, id = it.userID) {

                    }
                    MeDividerItem()
                }
            }
            items(meItems) {
                MeItem(icon = it.icon, title = it.title) {
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
fun MeItem(@DrawableRes icon: Int, title: String, click: () -> Unit = {}) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(Color.White)
        .clickable { click() }
        .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Image(mipmap = icon)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title, color = Color(0xFF1A1A1A), fontSize = 14.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(mipmap = R.mipmap.right_arrow)
    }
}

@Composable
fun MeInfoItem(avatar: String, nickname: String, id: String, click: () -> Unit) {
    val imageLoader = ImageLoader.Builder(LocalContext.current)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
            add(AnimatedPngDecoder.Factory())
        }
        .build()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { click() }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        androidx.compose.foundation.Image(
//            painter = rememberAsyncImagePainter(
//                avatar,
//                imageLoader = imageLoader
//            ),
//            contentDescription = null,
//            modifier = Modifier
//                .size(58.dp)
//                .clip(RoundedCornerShape(6.dp)),
//            contentScale = ContentScale.Crop
//        )
        AsyncImage(
            model = avatar,
            contentDescription = null,
            modifier = Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxHeight()
        ) {
            Text(text = nickname, fontSize = 14.sp, color = Color(0xFF1A1A1A))
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = id, fontSize = 10.sp, color = Color(0xFF4D4D4D)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Image(mipmap = R.mipmap.right_arrow)
    }
}

@Composable
fun MeDividerItem() {
    Divider(color = Color(0xFFE6E6E6), startIndent = 16.dp, thickness = 1.dp)
}

data class MeItemData(
    val icon: Int, val title: String, val route: String = ""
)


val meItems = listOf(
    MeItemData(R.mipmap.me_money, "钱包", Route.Wallet.route),
//    MeItemData(R.mipmap.me_service, "服务"),
//    MeItemData(R.mipmap.me_square, "我的广场"),
//    MeItemData(R.mipmap.me_grade, "我的评分"),
    MeItemData(R.mipmap.me_help, "帮助"),
    MeItemData(R.mipmap.me_setting, "设置")
)