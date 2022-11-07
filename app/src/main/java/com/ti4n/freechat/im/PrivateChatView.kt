package com.ti4n.freechat.im

import android.app.Activity
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.updateBounds
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.util.Minio
import com.ti4n.freechat.util.RecordVoiceUtil
import com.ti4n.freechat.util.coloredShadow
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.CustomPaddingTextField
import com.ti4n.freechat.widget.HomeTitle
import io.openim.android.sdk.models.Message
import io.openim.android.sdk.models.UserInfo
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PrivateChatView(
    navController: NavController, viewModel: PrivateChatViewModel = hiltViewModel()
) {
    var inputType by remember {
        mutableStateOf(InputType.Text)
    }
    val meInfo by viewModel.mineInfo.collectAsState()
    val messages = viewModel.messagePager.collectAsLazyPagingItems()
    val systemUiController = rememberSystemUiController()
    val toUserInfo by viewModel.toUserInfo.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val recordPermissionState = rememberPermissionState(
        android.Manifest.permission.RECORD_AUDIO
    )
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent
        )
        systemUiController.setNavigationBarColor(Color(0xFFF0F0F0))
    }
    var inputText by remember {
        mutableStateOf("")
    }
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let {
            scope.launch {
                IM.sendImageMessage(context, viewModel.toUserId, it)
            }
        }
    }
    var photoFile: File? = null
    val takePhoto =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                photoFile?.let {
                    scope.launch {
                        IM.sendImageMessage(viewModel.toUserId, it.absolutePath)
                    }
                }
            }
        }

    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        TopAppBar(backgroundColor = Color(0xFFF0F0F0), title = {
            HomeTitle(toUserInfo?.nickname ?: "")
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }, modifier = Modifier.statusBarsPadding(), elevation = 0.dp)
        LazyColumn(
            Modifier
                .weight(1f)
                .padding(bottom = 16.dp), reverseLayout = true
        ) {
            items(IM.newMessages.filter { it.recvID == viewModel.toUserId || it.sendID == viewModel.toUserId }
                .reversed()) {
                if (it.sendID == meInfo?.userID) {
                    MineMessage(message = it, navController = navController)
                } else {
                    ToUserMessage(message = it, navController = navController)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            items(messages.itemSnapshotList) {
                it?.let {
                    if (it.sendID == meInfo?.userID) {
                        MineMessage(message = it, navController = navController)
                    } else {
                        ToUserMessage(message = it, navController = navController)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .coloredShadow(blurRadius = 8.dp, color = Color(0xFFCCCCCC), offsetY = (-2).dp)
                .background(Color(0xFFF0F0F0))
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (inputType) {
                InputType.Voice -> Image(mipmap = R.mipmap.jianpan, modifier = Modifier.clickable {
                    inputType = InputType.Text
                })

                else -> Image(mipmap = R.mipmap.yuyin_chat, modifier = Modifier.clickable {
                    if (recordPermissionState.status == PermissionStatus.Granted) {
                        inputType = InputType.Voice
                    } else recordPermissionState.launchPermissionRequest()
                })
            }
            if (inputType == InputType.Voice) {
                Text(
                    text = "按住说话",
                    color = Color.Black,
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .background(
                            Color.White, RoundedCornerShape(4.dp)
                        )
                        .padding(vertical = 6.dp)
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    val startTime = System.currentTimeMillis()
                                    val savedFile = File(context.cacheDir, "${startTime}_voice.mp3")
                                    val recorder = RecordVoiceUtil(savedFile)
                                    try {
                                        recorder.record()
                                        awaitRelease()
                                    } finally {
                                        val endTime = System.currentTimeMillis()
                                        recorder.finish()
                                        IM.sendVoiceMessage(
                                            viewModel.toUserId,
                                            savedFile.absolutePath,
                                            (endTime - startTime) / 1000
                                        )
                                    }
                                },
                            )
                        },
                    textAlign = TextAlign.Center
                )
            } else {
                CustomPaddingTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    shape = RoundedCornerShape(4.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.White,
                        textColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .padding(vertical = 6.dp)
                        .weight(1f),
                    padding = PaddingValues(6.dp),
                    keyboardActions = KeyboardActions(onSend = {
                        scope.launch {
                            IM.sendTextMessage(viewModel.toUserId, inputText)
                            inputText = ""
                        }
                    }),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send)
                )
            }
//            Image(mipmap = R.mipmap.biaoqing, modifier = Modifier.clickable {
//                inputType = InputType.Emoji
//            })
            Image(mipmap = R.mipmap.more_type, modifier = Modifier.clickable {
                inputType = InputType.More
            })
        }
        AnimatedVisibility(
            visible = inputType == InputType.More,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            ) {
                Divider(color = Color(0xffe6e6e6), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    ItemMoreFunction(image = R.mipmap.camera_chat, text = R.string.camera) {
                        if (cameraPermissionState.status == PermissionStatus.Granted) {
                            photoFile =
                                File(context.cacheDir, "${System.currentTimeMillis()}_camera.jpg")
                            takePhoto.launch(
                                FileProvider.getUriForFile(
                                    context,
                                    "com.ti4n.freechat.provider",
                                    photoFile!!
                                )
                            )
                        } else {
                            cameraPermissionState.launchPermissionRequest()
                        }
                    }
                    ItemMoreFunction(image = R.mipmap.picture_chat, text = R.string.picture) {
                        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    ItemMoreFunction(image = R.mipmap.transfer_chat, text = R.string.transfer) {

                    }
                }
            }
        }
    }
}

enum class InputType {
    Text, Voice, Emoji, More
}

@Composable
fun ItemMoreFunction(@DrawableRes image: Int, @StringRes text: Int, click: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(mipmap = image,
            contentScale = ContentScale.Inside,
            modifier = Modifier
                .size(58.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
                .clickable { click() })
        Spacer(modifier = Modifier.height(5.dp))
        Text(text = stringResource(id = text), color = Color(0xFF666666), fontSize = 12.sp)
    }
}

@Composable
fun ToUserMessage(message: Message, navController: NavController) {
    val bgImg = ContextCompat.getDrawable(
        LocalContext.current, R.mipmap.chat_bg_others
    )
    Row(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        AsyncImage(
            model = message.senderFaceUrl,
            contentDescription = null,
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(3.45.dp))
                .clickable { navController.navigate(Route.Profile.jump(message.sendID)) }
        )
        Spacer(modifier = Modifier.width(6.dp))
        if (!message.pictureElem.snapshotPicture.url.isNullOrEmpty())
            AsyncImage(
                model = message.pictureElem.snapshotPicture.url,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .weight(1f)
            )
        else if (!message.soundElem.soundPath.isNullOrEmpty())
            Row(verticalAlignment = CenterVertically, modifier = Modifier
                .drawBehind {
                    bgImg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
                    bgImg?.draw(drawContext.canvas.nativeCanvas)
                }
                .clickable {
                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setAudioAttributes(
                        AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    mediaPlayer.setDataSource(message.soundElem.soundPath)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)) {
                Text(
                    text = "${message.soundElem.duration}”",
                    color = Color.Black,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Image(mipmap = R.mipmap.yuyin_r)
            }
        else
            Text(text = message.content,
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier
                    .drawBehind {
                        bgImg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
                        bgImg?.draw(drawContext.canvas.nativeCanvas)
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp))
        Spacer(modifier = Modifier.width(50.dp))
    }
}

@Composable
fun MineMessage(message: Message, navController: NavController) {
    val bgImg = ContextCompat.getDrawable(
        LocalContext.current, R.mipmap.chat_bg_mine
    )
    Row(
        Modifier
            .fillMaxWidth()
            .padding(end = 16.dp), horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.width(50.dp))
        if (!message.pictureElem.snapshotPicture.url.isNullOrEmpty())
            AsyncImage(
                model = message.pictureElem.snapshotPicture.url,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .weight(1f)
            )
        else if (!message.soundElem.soundPath.isNullOrEmpty())
            Row(verticalAlignment = CenterVertically, modifier = Modifier
                .drawBehind {
                    bgImg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
                    bgImg?.draw(drawContext.canvas.nativeCanvas)
                }
                .clickable {
                    val mediaPlayer = MediaPlayer()
                    mediaPlayer.setAudioAttributes(
                        AudioAttributes
                            .Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    mediaPlayer.setDataSource(message.soundElem.soundPath)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }
                .padding(horizontal = 12.dp, vertical = 8.dp)) {
                Image(mipmap = R.mipmap.yuyin_r)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "${message.soundElem.duration}”",
                    color = Color.Black,
                    fontSize = 16.sp,
                )
            }
        else
            Text(text = message.content,
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier
                    .drawBehind {
                        bgImg?.updateBounds(0, 0, size.width.toInt(), size.height.toInt())
                        bgImg?.draw(drawContext.canvas.nativeCanvas)
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp))
        Spacer(modifier = Modifier.width(6.dp))
        AsyncImage(
            model = message.senderFaceUrl,
            contentDescription = null,
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(3.45.dp))
                .clickable { navController.navigate(Route.Profile.jump(message.sendID)) }
        )
    }
}