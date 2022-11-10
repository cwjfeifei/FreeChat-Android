package com.ti4n.freechat.home

import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.login.ProfileInfoItem
import com.ti4n.freechat.util.AnimatedPngDecoder
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.util.IM.DEFAULT_FACEURL
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Row as Row1

private const val TAG = "MeEditView"

/**
 * Edit self profile view
 */
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MeEditView(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MeEditViewModel = hiltViewModel()
) {
    val me by IM.currentUserInfo.collectAsState()

    val faceURL by remember {
        mutableStateOf(viewModel.faceURL.value)
    }
    val nickname by remember {
        mutableStateOf(viewModel.nickname.value)
    }
    val gender by remember {
        mutableStateOf(viewModel.gender.value)
    }
    val email by remember {
        mutableStateOf(viewModel.email.value)
    }

    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
        systemUiController.setNavigationBarColor(Color(0xFFF0F0F0))
    }
    Scaffold(
        bottomBar = {
            TextButton(
                onClick = {
                    scope.launch {
                        // must be IM.login
                        val result = IM.setUserInfo(faceURL, nickname, gender, 0, email, null)
                        if (result is Unit) {
                            // success
                            navController.navigateUp()
                        } else {
                            // set user info failed
                            Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                Modifier
                    .height(42.dp)
                    .fillMaxWidth(), colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ), shape = RoundedCornerShape(0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        modifier = Modifier.navigationBarsPadding(),
        content = {
            Box(modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF0F0F0)) ) {
                IconButton(onClick = { navController.navigateUp() }, modifier = Modifier.padding(start = 16.dp, top = 16.dp)) {
                    Image(mipmap = R.mipmap.nav_back)
                }

                Column(
                    modifier = modifier
                        .fillMaxSize(),
                ) {
                    androidx.compose.foundation.Image(
                        painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(data = if (TextUtils.isEmpty(me?.faceURL)) DEFAULT_FACEURL else me?.faceURL)
                                .build(),
                            imageLoader = imageLoader
                        ),
                        contentDescription = null,
                        modifier = modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable {
                                navController.navigate(Route.EditPickFaceImage.route)
                            },
                        contentScale = ContentScale.FillBounds
                    )

                    LazyColumn(modifier = Modifier.background(Color.White)) {
                        item {
                            me?.let {
                                ProfileInfoItem(
                                    faceURL = faceURL,
                                    nickname = nickname,
                                    userID = me?.userID ?: "",
                                    gender = gender
                                ) {
                                    navController.navigate(Route.EditNickName.route)
                                }
                                MeDividerItem()

                                Row1(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .background(Color.White)
                                    .clickable { navController.navigate(Route.EditEmail.route) }
                                    .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically) {
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = stringResource(id = R.string.edit_email),
                                        color = Color.Black,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = email,
                                        color = Color(0xFF1A1A1A),
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 16.dp, end = 48.dp)
                                    )
                                    Image(mipmap = R.mipmap.right_arrow)
                                }
                            }
                        }
                    }
                }
            }

        })
}