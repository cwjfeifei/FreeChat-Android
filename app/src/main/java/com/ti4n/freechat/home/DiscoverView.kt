package com.ti4n.freechat.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.R
import com.ti4n.freechat.Route

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverView(modifier: Modifier = Modifier, navController: NavController) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFF0F0F0))
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(
                    0xFFF0F0F0
                )
            ),
            title = {
                Text(
                    text = "发现",
                    color = Color.Black,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            modifier = Modifier.statusBarsPadding()
        )
        LazyColumn(Modifier.background(Color.White)) {
            items(discoverItems) {
                DiscoverItem(icon = it.icon, title = it.title) {
                    if (it.route != "")
                        navController.navigate(it.route)
                }
                if (it.needSection) {
                    DiscoverSectionItem()
                } else if (it.needDivider) {
                    DiscoverDividerItem()
                }
            }
            item {
                Divider(color = Color(0xFFF0F0F0), thickness = 16.dp)
            }
        }
    }
}

@Composable
fun DiscoverItem(@DrawableRes icon: Int, title: String, click: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White)
            .clickable { click() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(mipmap = icon)
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            color = Color(0xFF1A1A1A),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(mipmap = R.mipmap.right_arrow)
    }
}

@Composable
fun DiscoverDividerItem() {
    Divider(color = Color(0xFFE6E6E6), startIndent = 52.dp, thickness = 1.dp)
}

@Composable
fun DiscoverSectionItem() {
    Divider(color = Color(0xFFF0F0F0), thickness = 8.dp)
}

sealed interface DiscoverType {
    data class DiscoverItem(
        val icon: Int,
        val title: String,
        val route: String = "",
        val needDivider: Boolean = true,
        val needSection: Boolean = false,
    ) : DiscoverType
}

val discoverItems = listOf(
    DiscoverType.DiscoverItem(R.mipmap.community, "FreeChat自治社区"),
    DiscoverType.DiscoverItem(R.mipmap.star, "自由星球"),
    DiscoverType.DiscoverItem(R.mipmap.circle, "圈子"),
    DiscoverType.DiscoverItem(R.mipmap.buy, "购物", needDivider = false, needSection = true),
    DiscoverType.DiscoverItem(R.mipmap.yingyong, "应用中心"),
    DiscoverType.DiscoverItem(R.mipmap.routine, "小程序", needDivider = false, needSection = true),
    DiscoverType.DiscoverItem(R.mipmap.exchange, "闪兑"),
    DiscoverType.DiscoverItem(R.mipmap.loan, "借贷"),
    DiscoverType.DiscoverItem(
        R.mipmap.transaction,
        "交易",
        needDivider = false,
        needSection = true
    ),
    DiscoverType.DiscoverItem(R.mipmap.see, "看一看"),
    DiscoverType.DiscoverItem(R.mipmap.listen, "听一听"),
    DiscoverType.DiscoverItem(R.mipmap.scan, "扫一扫"),
    DiscoverType.DiscoverItem(R.mipmap.slither, "划一划", needDivider = false, needSection = true),
    DiscoverType.DiscoverItem(R.mipmap.browser, "区块浏览器"),
    DiscoverType.DiscoverItem(
        R.mipmap.net,
        "分布式加密网络",
        needDivider = false,
        needSection = true
    ),
)