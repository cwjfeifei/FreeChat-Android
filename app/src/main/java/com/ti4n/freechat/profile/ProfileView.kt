package com.ti4n.freechat.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.CustomPaddingTextField
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

@Composable
fun ProfileView(
    navController: NavController,
    isFromFriendApplication: Boolean = false,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val isFriend by viewModel.isFriend.collectAsState()
    val isSelf by viewModel.isSelf.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()
    var isFromFriendApplication by remember {
        mutableStateOf(isFromFriendApplication)
    }
    var showRefuseDialog by remember {
        mutableStateOf(false)
    }
    var refuseMessage by remember {
        mutableStateOf("")
    }
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent
        )
        systemUiController.setNavigationBarColor(Color(0xFFF0F0F0))
    }
    LaunchedEffect(viewModel.toUserId) {
        viewModel.refuseSuccess.filter { it }.collectLatest {
            showRefuseDialog = false
            isFromFriendApplication = false
        }
    }
    LaunchedEffect(viewModel.toUserId) {
        viewModel.approveSuccess.filter { it }.collectLatest {
            isFromFriendApplication = false
        }
    }
    Scaffold(bottomBar = {
        if (isFromFriendApplication && !isFriend && !isSelf) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(3.dp)) {
                TextButton(
                    onClick = {
                        showRefuseDialog = true
                    }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFED5B56), contentColor = Color.White
                    ), shape = RoundedCornerShape(0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.refuse),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
                TextButton(
                    onClick = {
                        navController.navigate(Route.ApproveFriendApplication.route)
                    }, colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                    ), shape = RoundedCornerShape(0.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.accept),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        } else if (!isSelf) {
            TextButton(
                onClick = {
                    if (isFriend) navController.navigate(
                        Route.PrivateChat.jump(
                            viewModel.toUserId,
                            IM.getConversationId(viewModel.toUserId)
                                .ifEmpty { "single_${viewModel.toUserId}" }
                        )
                    ) else navController.navigate(Route.SendFriendApplication.route)
                }, colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ), shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
            ) {
                if (isFriend) {
                    Image(mipmap = R.mipmap.message)
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = stringResource(id = if (isFriend) R.string.send_message else R.string.apply_tobe_friend),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
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
                        userInfo?.faceURL ?: "",
                        userInfo?.nickname ?: "",
                        userInfo?.remark ?: "",
                        userInfo?.userID ?: "",
                        userInfo?.gender ?: 1,
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
                if (!isSelf && isFriend) item {
                    Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
                    ProfileItem("备注") {
                        navController.navigate(Route.SetRemark.route)
                    }
                    Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
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
        if (showRefuseDialog)
            Dialog(onDismissRequest = {
                showRefuseDialog = false
                refuseMessage = ""
            }) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = stringResource(id = R.string.reply),
                        color = Color(0xFF1A1A1A),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CustomPaddingTextField(
                        value = refuseMessage,
                        onValueChange = { refuseMessage = it },
                        padding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            textColor = Color(0xFF181818),
                            backgroundColor = Color(0xFFF5F5F5)
                        ),
                        modifier = Modifier.height(30.dp),
                        shape = RoundedCornerShape(2.dp)
                    )
                    Spacer(modifier = Modifier.height(35.dp))
                    Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp)
                    Row(Modifier.fillMaxWidth(), verticalAlignment = CenterVertically) {
                        TextButton(
                            onClick = {
                                showRefuseDialog = false
                                refuseMessage = ""
                            },
                            modifier = Modifier
                                .height(47.dp)
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                                contentColor = Color(0xFF1B1B1B)
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.cancel),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Box(
                            Modifier
                                .height(47.dp)
                                .width(0.5.dp)
                                .background(Color(0xFFEBEBEB))
                        )
                        TextButton(
                            onClick = {
                                viewModel.refuseFriendApplication()
                            },
                            modifier = Modifier
                                .height(47.dp)
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                                contentColor = Color(0xFF4A84F7)
                            )
                        ) {
                            Text(
                                text = stringResource(id = R.string.send),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
    }
}

@Composable
fun ProfileInfoItem(
    avatar: String, nickname: String, mark: String, id: String, gender: Int
//    location: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatar,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.height(64.dp), verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = CenterVertically) {
                Text(
                    text = mark.ifEmpty { nickname },
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(6.dp))
                Image(mipmap = if (gender == 1) R.mipmap.male else R.mipmap.female)
            }
            Spacer(modifier = Modifier.weight(1f))
            if (mark.isNotEmpty()) Text(
                text = "昵称：$nickname", fontSize = 10.sp, color = Color(0xFF4D4D4D)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "FCID：$id", fontSize = 12.sp, color = Color(0xFF808080))
            Spacer(modifier = Modifier.height(5.dp))
//            Text(text = "地区：$location", fontSize = 10.sp, color = Color(0xFF4D4D4D))
        }
    }
}

@Composable
fun ProfileItem(title: String, click: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { click() }
            .padding(16.dp),
        verticalAlignment = CenterVertically) {
        Text(text = title, fontSize = 14.sp, color = Color(0xFF1A1A1A))
        Spacer(modifier = Modifier.weight(1f))
        Image(mipmap = R.mipmap.right_arrow)
    }
}