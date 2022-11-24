package com.ti4n.freechat.login

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.ti4n.freechat.R
import com.ti4n.freechat.util.AnimatedPngDecoder
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.Image
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.coerceAtMost
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.profile.ProfileInfoItem
import com.ti4n.freechat.widget.DefaultProfileLargeImage

// Self Preview
@Composable
fun ProfilePreview(
    navController: NavController,
    xfaceURL: String,  // need encode
    nickname: String,
    gender: Int
) {

    val userID by lazy {
        IM.currentUserInfo.value?.userID
    }
    val faceURL = URLDecoder.decode(xfaceURL, StandardCharsets.UTF_8.toString())

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).components {
        if (Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
        add(AnimatedPngDecoder.Factory())
    }.build()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp + WindowInsets.systemBars.asPaddingValues()
        .calculateTopPadding()
    val density = LocalDensity.current
    var userInfoOffsetPx by remember {
        mutableStateOf(screenHeight - 100.dp - 42.dp - 280.dp)
    }
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent
        )
        systemUiController.setNavigationBarColor(Color.White)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        if ((faceURL?.ifEmpty { IM.DEFAULT_FACEURL }
                ?: IM.DEFAULT_FACEURL) == IM.DEFAULT_FACEURL)
            DefaultProfileLargeImage(isSelf = true)
        else
            androidx.compose.foundation.Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(data = faceURL)
                        .build(),
                    imageLoader = imageLoader,
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight - 100.dp),
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
                    y = userInfoOffsetPx
                        .coerceAtLeast(0.dp)
                        .coerceAtMost(screenHeight - 100.dp - 42.dp - 280.dp)
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
                                delta.toDp()
                            }
                        })
            ) {
                ProfileInfoItem(
                    faceURL,
                    nickname,
                    "",
                    userID ?: "",
                    gender
                )
            }
        }
        TextButton(
            onClick = {
            },
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF3879FD), contentColor = Color.White
            ),
            shape = RoundedCornerShape(0.dp),
        ) {
            Text(
                text = stringResource(id = R.string.send_message),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

    }
}