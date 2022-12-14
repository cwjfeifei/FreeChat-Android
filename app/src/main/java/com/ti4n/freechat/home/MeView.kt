package com.ti4n.freechat.home

import android.os.Build.VERSION.SDK_INT
import android.text.TextUtils
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.AnimatedPngDecoder
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.util.IM.DEFAULT_FACEURL
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.MiddleEllipsisText

@Composable
fun MeView(
    modifier: Modifier = Modifier,
    navController: NavController
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
        systemUiController.setNavigationBarColor(Color.White)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        androidx.compose.foundation.Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context)
                    .data(data = "https://img.ithome.com/newsuploadfiles/focus/3baf5060-7091-449e-8809-548e5b5369aa.jpg?x-bce-process=image/format,f_auto")
                    .build(),
                imageLoader = imageLoader,
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        LazyColumn(modifier = Modifier.background(Color.White)) {
            me.let {
                item {
                    MeInfoItem(
                        faceURL = it?.faceURL ?: "",
                        nickname = it?.nickname ?: "",
                        userID = it?.userID ?: "",
                        gender = it?.gender ?: 1
                    ) {
                        navController.navigate(Route.MeEdit.route)
                    }
                    MeDividerItem()
                }
            }
            items(meItems) {
                MeItem(icon = it.icon, title = stringResource(id = it.title)) {
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
        .clickableSingle { click() }
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
fun MeInfoItem(
    userID: String,
    faceURL: String,
    nickname: String,
    gender: Int, click: () -> Unit
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxWidth()
        .height(104.dp)
        .clickableSingle {
            click()
        }) {

        val (faceView, nicknameView, genderView, uidView, rightArrow) = createRefs()
        AsyncImage(
            model = if (TextUtils.isEmpty(faceURL)) DEFAULT_FACEURL else faceURL,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(faceView) {
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                }
                .size(64.dp)
                .clip(
                    RoundedCornerShape(4.dp)
                )
        )

        Text(
            text = nickname,
            modifier = Modifier
                .constrainAs(nicknameView) {
                    top.linkTo(faceView.top)
                    start.linkTo(faceView.end, margin = 12.dp)
                }
                .widthIn(0.dp, 240.dp),
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.Black
        )
        when (gender) {
            1 -> Image(
                mipmap = R.mipmap.male,
                modifier = Modifier
                    .constrainAs(genderView) {
                        top.linkTo(faceView.top)
                        start.linkTo(nicknameView.end, margin = 6.dp)
                    }
                    .size(16.dp)
            )

            2 -> Image(
                mipmap = R.mipmap.female,
                modifier = Modifier
                    .constrainAs(genderView) {
                        top.linkTo(faceView.top)
                        start.linkTo(nicknameView.end, margin = 6.dp)
                    }
                    .size(16.dp)
            )

            3 -> Image(
                mipmap = R.mipmap.transgender,
                modifier = Modifier
                    .constrainAs(genderView) {
                        top.linkTo(faceView.top)
                        start.linkTo(nicknameView.end, margin = 6.dp)
                    }
                    .size(16.dp)
            )
        }

        MiddleEllipsisText(
            text = "FCID: $userID",
            modifier = Modifier
                .constrainAs(uidView) {
                    bottom.linkTo(faceView.bottom)
                    start.linkTo(nicknameView.start)
                }
                .widthIn(0.dp, 240.dp),
            fontSize = 12.sp,
            color = Color(0xFF808080)
        )

        Image(mipmap = R.mipmap.right_arrow,
            modifier = Modifier.constrainAs(rightArrow) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end, 16.dp)
            }
        )
    }
}
//@Composable
//fun MeInfoItem(avatar: String, nickname: String, id: String, click: () -> Unit) {
//    val imageLoader = ImageLoader.Builder(LocalContext.current)
//        .components {
//            if (SDK_INT >= 28) {
//                add(ImageDecoderDecoder.Factory())
//            } else {
//                add(GifDecoder.Factory())
//            }
//            add(AnimatedPngDecoder.Factory())
//        }
//        .build()
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickableSingle { click() }
//            .padding(horizontal = 16.dp, vertical = 20.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
////        androidx.compose.foundation.Image(
////            painter = rememberAsyncImagePainter(
////                avatar,
////                imageLoader = imageLoader
////            ),
////            contentDescription = null,
////            modifier = Modifier
////                .size(58.dp)
////                .clip(RoundedCornerShape(6.dp)),
////            contentScale = ContentScale.Crop
////        )
//        AsyncImage(
//            model = avatar,
//            contentDescription = null,
//            modifier = Modifier
//                .size(58.dp)
//                .clip(RoundedCornerShape(6.dp)),
//            contentScale = ContentScale.Crop
//        )
//        Spacer(modifier = Modifier.width(12.dp))
//        Column(
//            modifier = Modifier
//                .padding(vertical = 6.dp)
//                .fillMaxHeight()
//        ) {
//            Text(text = nickname, fontSize = 14.sp, color = Color(0xFF1A1A1A))
//            Spacer(modifier = Modifier.weight(1f))
//            Text(
//                text = id, fontSize = 10.sp, color = Color(0xFF4D4D4D)
//            )
//        }
//        Spacer(modifier = Modifier.weight(1f))
//        Image(mipmap = R.mipmap.right_arrow)
//    }
//}

@Composable
fun MeDividerItem() {
    Divider(color = Color(0xFFEBEBEB), startIndent = 16.dp, thickness = 0.5.dp)
}

data class MeItemData(
    val icon: Int, @StringRes val title: Int, val route: String = ""
)


val meItems = listOf(
    MeItemData(R.mipmap.me_money, R.string.wallet, Route.Wallet.route),
//    MeItemData(R.mipmap.me_service, "??????"),
//    MeItemData(R.mipmap.me_square, "????????????"),
//    MeItemData(R.mipmap.me_grade, "????????????"),
    MeItemData(R.mipmap.me_help, R.string.help),
    MeItemData(R.mipmap.me_setting, R.string.setting, Route.Setting.route)
)