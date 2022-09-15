package com.ti4n.freechat.home

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.widget.Image

@Composable
fun MeView(modifier: Modifier, navController: NavController) {
    val systemUiController = rememberSystemUiController()
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
        Image(
            mipmap = R.mipmap.mine_bg,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(375 / 260f),
            contentScale = ContentScale.FillBounds
        )
        LazyColumn(modifier = Modifier.background(Color.White)) {
            items(meItems) {
                when (it) {
                    is MeType.MeInfoItem -> MeInfoItem(
                        avatar = it.avatar,
                        nickname = it.nickname,
                        id = it.id
                    )
                    is MeType.MeItem -> MeItem(icon = it.icon, title = it.title)
                }
                MeDividerItem()
            }
        }
    }
}

@Composable
fun MeItem(@DrawableRes icon: Int, title: String, route: String = "") {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(mipmap = icon)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color(0xFF1A1A1A),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(mipmap = R.mipmap.right_arrow)
    }
}

@Composable
fun MeInfoItem(avatar: String, nickname: String, id: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatar,
            contentDescription = null, modifier = Modifier
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
                text = id,
                fontSize = 10.sp,
                color = Color(0xFF4D4D4D)
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

sealed interface MeType {
    data class MeItem(
        val icon: Int,
        val title: String,
        val route: String = ""
    ) : MeType

    data class MeInfoItem(
        val avatar: String,
        val nickname: String,
        val id: String,
        val route: String = ""
    ) : MeType
}


val meItems = listOf(
    MeType.MeInfoItem(
        "https://i1.wp.com/buondua.art/cdn/26551/BLUECAKE-Son-Ye-Eun-REDHOOD-SM-MrCong.com-016.jpeg",
        "想要的爱情",
        "FCID:12231XSXSA12312"
    ),
    MeType.MeItem(R.mipmap.me_money, "钱包"),
    MeType.MeItem(R.mipmap.me_service, "服务"),
    MeType.MeItem(R.mipmap.me_square, "我的广场"),
    MeType.MeItem(R.mipmap.me_grade, "我的评分"),
    MeType.MeItem(R.mipmap.me_help, "帮助"),
    MeType.MeItem(R.mipmap.me_setting, "设置")
)