package com.ti4n.freechat.login

import android.os.Build
import android.text.TextUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.ti4n.freechat.R
import com.ti4n.freechat.util.AnimatedPngDecoder
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.Image

// Self Preview
@Composable
fun ProfilePreview(
    navController: NavController,
    viewModel: RegisterViewModel
) {

    val userID by lazy {
        IM.currentUserInfo.value?.userID
    }
    var faceURL by remember {
        mutableStateOf(viewModel.faceURL.value)
    }
    var gender by remember {
        mutableStateOf(viewModel.gender.value)
    }

    var nickname by remember {
        mutableStateOf(viewModel.name.value)
    }

    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context).components {
        if (Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
        add(AnimatedPngDecoder.Factory())
    }.build()

    LaunchedEffect(Unit) {
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .systemBarsPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 24.dp)
                    .systemBarsPadding()
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
                }
            }

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
                    .weight(1f),
                contentScale = ContentScale.Crop
            )
            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)

            ProfileInfoItem(
                userID = userID ?: "",
                faceURL = faceURL,
                nickname = nickname,
                gender = gender
            ) {
            }

            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
            TextButton(
                onClick = {
                },
                Modifier
                    .height(42.dp)
                    .fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ), shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.send_message),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ProfileInfoItem(
    userID: String,
    faceURL: String,
    nickname: String,
    gender: Int, click: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .background(color = Color.White)
            .clickable {
                click()
            }) {

        val (faceView, nicknameView, genderView, uidView) = createRefs()
        AsyncImage(
            model = if (TextUtils.isEmpty(faceURL)) IM.DEFAULT_FACEURL else faceURL,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(faceView) {
                    top.linkTo(parent.top, margin = 20.dp)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                }
                .size(64.dp)
                .clip(
                    RoundedCornerShape(4.dp)
                )
        )

        Text(
            text = if (TextUtils.isEmpty(nickname)) "未设置昵称" else nickname,
            modifier = Modifier
                .constrainAs(nicknameView) {
                    top.linkTo(faceView.top)
                    start.linkTo(faceView.end, margin = 12.dp)
                }
                .widthIn(0.dp, 160.dp),
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

        // Assign reference "text" to the Text composable
        // and constrain it to the bottom of the Button composable
        Text(
            text = "FCCID:$userID",
            modifier = Modifier
                .constrainAs(uidView) {
                    bottom.linkTo(faceView.bottom)
                    start.linkTo(nicknameView.start)
                }
                .widthIn(0.dp, 240.dp),
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color(0xFF808080)
        )
    }
}