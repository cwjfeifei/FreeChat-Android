package com.ti4n.freechat.home

import androidx.compose.foundation.background
import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.SearchView

@Composable
fun SquareView(modifier: Modifier = Modifier, navController: NavController) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    var selectedTab by remember {
        mutableStateOf(SquareTab.Suggest)
    }
    val showSearchView = remember {
        mutableStateOf(false)
    }
    val searchText = remember {
        mutableStateOf("")
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            backgroundColor = Color(0xFFF0F0F0),
            elevation = 0.dp,
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Image(mipmap = R.mipmap.filter)
            Spacer(modifier = Modifier.weight(1f))
            SquareTab.values().forEach {
                Text(
                    text = it.title,
                    fontSize = if (selectedTab == it) 17.sp else 14.sp,
                    fontWeight = if (selectedTab == it) FontWeight.Medium else FontWeight.Normal,
                    color = if (selectedTab == it) Color(0xFF6F5FFC) else Color(0xFF666666),
                    modifier = Modifier
                        .clickableSingle { selectedTab = it }
                        .padding(horizontal = 10.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Image(mipmap = R.mipmap.camera)
        }
        SearchView(showSearchView = showSearchView, searchText = searchText)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            item {
                SquareItemNormal(
                    "https://i1.wp.com/buondua.art/cdn/26551/BLUECAKE-Son-Ye-Eun-REDHOOD-SM-MrCong.com-001.jpeg?q=90",
                    "????????????",
                    "????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????2013?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????",
                    true,
                    52,
                    52,
                    listOf(
                        "https://i1.wp.com/buondua.art/cdn/26551/BLUECAKE-Son-Ye-Eun-REDHOOD-SM-MrCong.com-005.jpeg",
                        "https://i1.wp.com/buondua.art/cdn/26551/BLUECAKE-Son-Ye-Eun-REDHOOD-SM-MrCong.com-016.jpeg"
                    ), navController
                )
            }
        }
    }
}

enum class SquareTab(val title: String) {
    Like("??????"), Nearby("??????"), Friend("??????"), Suggest("??????")
}

@Composable
fun BaseSquareItem(
    avatar: String,
    name: String,
    write: String,
    isLike: Boolean,
    likeCount: Int,
    commentCount: Int,
    navController: NavController,
    media: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = 20.dp, bottom = 10.dp, start = 24.dp, end = 24.dp)
    ) {
        AsyncImage(
            model = avatar,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(
                    RoundedCornerShape(4.dp)
                )
                .clickableSingle { navController.navigate(Route.Profile.route) },
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = name,
                color = Color(0xFF636780),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = write,
                color = Color(0xFF1B1B1B),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            media()
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Image(mipmap = if (isLike) R.mipmap.collect_sel else R.mipmap.collect_nor)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "$likeCount", color = Color.Black, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Image(mipmap = R.mipmap.comment)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "$commentCount", color = Color.Black, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SquareItemNormal(
    avatar: String,
    name: String,
    write: String,
    isLike: Boolean,
    likeCount: Int,
    commentCount: Int,
    images: List<String>,
    navController: NavController
) {
    BaseSquareItem(avatar, name, write, isLike, likeCount, commentCount, navController) {
        FlowRow {
            images.forEach {
                AsyncImage(
                    model = it, contentDescription = null, modifier = Modifier
                        .padding(top = 8.dp, end = 8.dp)
                        .size(78.dp)
                        .clip(
                            RoundedCornerShape(4.dp)
                        )
                        .clickableSingle {
//                            navController.navigate(
//                                Route.BigImage.route,
//                                "url" to Url(it)
//                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun SquareItemAd(
    avatar: String,
    name: String,
    write: String,
    isLike: Boolean,
    likeCount: Int,
    commentCount: Int,
) {

}