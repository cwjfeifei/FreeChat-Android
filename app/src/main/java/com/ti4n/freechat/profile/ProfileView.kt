package com.ti4n.freechat.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.home.*
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.CommentGrade
import com.ti4n.freechat.widget.CommentItem
import com.ti4n.freechat.widget.Image

@Composable
fun ProfileView(navController: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val systemUiController = rememberSystemUiController()
    val isFriend by viewModel.isFriend.collectAsState()
    val isSelf by viewModel.isSelf.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent
        )
        systemUiController.setNavigationBarColor(Color(0xFFF0F0F0))
    }
    Scaffold(bottomBar = {
        if (!isSelf)
            BottomNavigation(
                backgroundColor = Color.White, elevation = 8.dp
            ) {
                if (isFriend) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .clickable {
                                navController.navigate(
                                    Route.PrivateChat.jump(
                                        viewModel.toUserId,
                                        IM.getConversationId(viewModel.toUserId)
                                    )
                                )
                            },
                        contentAlignment = Center
                    ) {
                        Text(
                            text = "发消息",
                            color = Color(0xFF6F5FFC),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
//                Box(
//                    modifier = Modifier
//                        .width(7.dp)
//                        .fillMaxHeight()
//                        .background(Color(0xFFF0F0F0))
//                )
//                Box(
//                    modifier = Modifier
//                        .fillMaxHeight()
//                        .weight(1f)
//                        .clickable { navController.navigate(Route.VideoVoiceChatBottom.route) },
//                    contentAlignment = Center
//                ) {
//                    Text(
//                        text = "音视频通话",
//                        color = Color(0xFF6F5FFC),
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Medium
//                    )
//                }
                } else {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .clickable { viewModel.addFriend() }, contentAlignment = Center
                    ) {
                        Text(
                            text = "申请加为好友",
                            color = Color(0xFF6F5FFC),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
    }, modifier = Modifier.navigationBarsPadding()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0))
                .padding(it),
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    mipmap = R.mipmap.mine_bg,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(700 / 428f),
                    contentScale = ContentScale.FillBounds
                )
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    backgroundColor = Color.Transparent,
                    title = {},
                    navigationIcon = {
                        Image(mipmap = R.mipmap.me_back,
                            modifier = Modifier
                                .clickable { navController.navigateUp() }
                                .padding(horizontal = 16.dp))
                    },
                    elevation = 0.dp
                )
            }
            LazyColumn(modifier = Modifier.background(Color.White)) {
                item {
                    ProfileInfoItem(
                        userInfo.faceURL ?: "",
                        userInfo.nickname ?: "",
                        userInfo.remark ?: "",
                        userInfo.userID ?: "",
                        ""
                    )
                    Divider(color = Color(0xFFF0F0F0))
                }
//                item {
//                    Row(
//                        Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(text = "我的广场", fontSize = 12.sp, color = Color(0xFF1A1A1A))
//                        Spacer(modifier = Modifier.width(18.dp))
//                        AsyncImage(
//                            model = "https://i1.wp.com/buondua.art/cdn/26551/BLUECAKE-Son-Ye-Eun-REDHOOD-SM-MrCong.com-005.jpeg",
//                            contentDescription = null,
//                            modifier = Modifier
//                                .padding(end = 8.dp)
//                                .size(44.dp)
//                                .clip(RoundedCornerShape(6.dp)),
//                            contentScale = ContentScale.Crop
//                        )
//                        Spacer(modifier = Modifier.weight(1f))
//                        Spacer(modifier = Modifier.width(18.dp))
//                        Image(mipmap = R.mipmap.right_arrow)
//                    }
//                    Divider(color = Color(0xFFF0F0F0), thickness = 8.dp)
//                }
//                item {
//                    CommentGrade(
//                        grade = 4.6f,
//                        totalCount = 878,
//                        fiveCount = 400,
//                        fourCount = 216,
//                        threeCount = 100,
//                        twoCount = 50,
//                        oneCount = 2
//                    )
//                    Divider(color = Color(0xFFF0F0F0), thickness = 8.dp)
//                }
//                item {
//                    CommentItem(
//                        name = "Mohammad",
//                        comment = "这是一个非常好的聊友，很有趣。",
//                        grade = 4.5f,
//                        time = System.currentTimeMillis()
//                    )
//                    Divider(color = Color(0xFFE6E6E6), thickness = 1.dp)
//                }
//                item {
//                    ProfileItem("评论Ta")
//                    Divider(color = Color(0xFFE6E6E6), thickness = 1.dp)
//                }
                if (!isSelf)
                    item {
                        ProfileItem("备注")
                        Divider(color = Color(0xFFE6E6E6), thickness = 1.dp)
                    }
//                item {
//                    ProfileItem("权限设置")
//                    Divider(color = Color(0xFFE6E6E6), thickness = 1.dp)
//                }
//
//                item {
//                    Divider(color = Color(0xFFF0F0F0), thickness = 20.dp)
//                }
            }
        }
    }
}

@Composable
fun ProfileInfoItem(
    avatar: String,
    nickname: String,
    mark: String,
    id: String,
    location: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(108 / 428f)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = mark.ifEmpty { nickname }, fontSize = 14.sp, color = Color(0xFF1A1A1A))
            if (mark.isNotEmpty()) Text(
                text = "昵称：$nickname",
                fontSize = 10.sp,
                color = Color(0xFF4D4D4D)
            )
            Text(text = "FCID：$id", fontSize = 10.sp, color = Color(0xFF4D4D4D))
//            Text(text = "地区：$location", fontSize = 10.sp, color = Color(0xFF4D4D4D))
        }
    }
}

@Composable
fun ProfileItem(title: String, route: String = "") {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp), verticalAlignment = CenterVertically
    ) {
        Text(text = title, fontSize = 14.sp, color = Color(0xFF1A1A1A))
        Spacer(modifier = Modifier.weight(1f))
        Image(mipmap = R.mipmap.right_arrow)
    }
}