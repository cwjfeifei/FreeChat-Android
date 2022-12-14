package com.ti4n.freechat.profile

import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.compose.foundation.background
import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.ti4n.freechat.widget.CustomPaddingTextField
import com.ti4n.freechat.widget.DefaultProfileLargeImage
import com.ti4n.freechat.widget.Image
import io.openim.android.sdk.models.FriendInfo
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlin.math.roundToInt

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
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp + WindowInsets.systemBars.asPaddingValues()
        .calculateTopPadding()
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).components {
        if (Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
        add(AnimatedPngDecoder.Factory())
    }.build()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent
        )
        systemUiController.setNavigationBarColor(Color.White)
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
    val density = LocalDensity.current
    var userInfoOffsetPx by rememberSaveable {
        mutableStateOf((screenHeight - 100.dp - (if (isSelf) 0.dp else 42.dp) - 280.dp).value)
    }
    Scaffold(bottomBar = {
        if (isFromFriendApplication && !isFriend && !isSelf) {
            Row(
                Modifier.fillMaxWidth(), Arrangement.spacedBy(3.dp)
            ) {
                TextButton(
                    onClick = {
                        showRefuseDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFED5B56), contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.weight(1f),
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
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(0.dp),
                    modifier = Modifier.weight(1f),
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
                        Route.PrivateChat.jump(viewModel.toUserId,
                            IM.getConversationId(viewModel.toUserId)
                                .ifEmpty { "single_${viewModel.toUserId}" })
                    ) else navController.navigate(
                        Route.SendFriendApplication.route
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.fillMaxWidth(),
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
    }, modifier = Modifier.navigationBarsPadding(), backgroundColor = Color.White) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it), contentAlignment = Alignment.TopCenter
        ) {
            if ((userInfo?.faceURL?.ifEmpty { DEFAULT_FACEURL }
                    ?: DEFAULT_FACEURL) == DEFAULT_FACEURL)
                DefaultProfileLargeImage(isSelf = isSelf)
            else
                androidx.compose.foundation.Image(
                    painter = rememberAsyncImagePainter(
                        ImageRequest.Builder(context)
                            .data(data = userInfo?.faceURL ?: DEFAULT_FACEURL)
                            .build(), imageLoader = imageLoader, contentScale = ContentScale.Crop
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight - 100.dp - (if (isSelf) 0.dp else 42.dp)),
                    contentScale = ContentScale.Crop
                )
            TopAppBar(
                modifier = Modifier.statusBarsPadding(),
                backgroundColor = Color.Transparent,
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Image(mipmap = R.mipmap.me_back)
                    }
                },
                elevation = 0.dp
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent)
                    .offset(
                        y = Dp(userInfoOffsetPx)
                            .coerceAtLeast(0.dp)
                            .coerceAtMost(screenHeight - 100.dp - (if (isSelf) 0.dp else 42.dp) - 280.dp)
                    )
            ) {
                Spacer(
                    modifier = Modifier
                        .height(280.dp)
                        .background(Color.Transparent)
                )
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .weight(1f)
                        .draggable(orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                userInfoOffsetPx += with(density) {
                                    delta.toDp().value
                                }
                            })
                ) {
                    ProfileInfoItem(
                        userInfo?.faceURL ?: DEFAULT_FACEURL,
                        userInfo?.nickname ?: "",
                        userInfo?.remark ?: "",
                        userInfo?.userID ?: "",
                        userInfo?.gender ?: 1
                    )
                    if (!isSelf && isFriend) {
                        ProfileItem(stringResource(id = R.string.remark)) {
                            navController.navigate(Route.SetRemark.route)
                        }
                        Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
                    }
                }
            }
        }
    }
    if (showRefuseDialog) Dialog(onDismissRequest = {
        showRefuseDialog = false
        refuseMessage = ""
    }) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp)), horizontalAlignment = Alignment.CenterHorizontally
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
                        backgroundColor = Color.White, contentColor = Color(0xFF1B1B1B)
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
                        viewModel.refuseFriendApplication(refuseMessage)
                    },
                    modifier = Modifier
                        .height(47.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.White, contentColor = Color(0xFF4A84F7)
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

@Composable
fun ProfileInfoItem(
    avatar: String,
    nickname: String,
    mark: String,
    id: String,
    gender: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(40.dp, 4.dp)
                .background(
                    Color(0xFF1A1A1A), RoundedCornerShape(3.dp)
                )
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
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
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.widthIn(0.dp, 240.dp),
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    when (gender) {
                        1 -> Image(mipmap = R.mipmap.male)

                        2 -> Image(mipmap = R.mipmap.female)

                        3 -> Image(mipmap = R.mipmap.transgender)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (mark.isNotEmpty()) Text(
                    text = "?????????$nickname", fontSize = 10.sp, color = Color(0xFF4D4D4D)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "FCID???$id", fontSize = 12.sp, color = Color(0xFF808080))
                Spacer(modifier = Modifier.height(5.dp))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
    Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
}

@Composable
fun ProfileItem(title: String, click: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickableSingle { click() }
            .padding(16.dp),
        verticalAlignment = CenterVertically) {
        Text(text = title, fontSize = 14.sp, color = Color(0xFF1A1A1A))
        Spacer(modifier = Modifier.weight(1f))
        Image(mipmap = R.mipmap.right_arrow)
    }
}