package com.ti4n.freechat.home

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import android.os.Build.VERSION.SDK_INT
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
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
import com.ti4n.freechat.login.ProfileInfoItem
import com.ti4n.freechat.util.AnimatedPngDecoder
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.launch
import java.util.*

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
    val birth by remember {
        mutableStateOf(viewModel.birth.value)
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
        topBar = {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .background(color = Color(0xFFF5F5F5))
                    ) {
                val (back, title) = createRefs()
                IconButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.constrainAs(back) {
                        top.linkTo(parent.top, margin = 20.dp)
                        bottom.linkTo(parent.bottom, margin = 0.dp)
                        start.linkTo(parent.start, margin = 16.dp)
                    }
                ) {
                    Image(mipmap = R.mipmap.nav_back)
                }

                Text(
                    text = stringResource(id = R.string.faceurl),
                    color = Color.Black,
                    fontSize = 14.sp,
                    modifier = Modifier.constrainAs(title) {
                        top.linkTo(parent.top, margin = 20.dp)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )
            }
        },
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
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .background(Color(0xFFF0F0F0))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {
                    item {
                        me?.let {
                            MePropertyFace(faceURL = faceURL) {
                                navController.navigate(Route.PickFaceImage.route)
                            }

                            MePropertyItem(
                                label = stringResource(id = R.string.name),
                                value = nickname
                            ) {
                                navController.navigate(Route.EditNickName.route)
                            }

                            MePropertyItem(
                                label = stringResource(id = R.string.gender),
                                value = if (gender == 1) "男" else if (gender == 2) "女" else "跨性别",
                            ) {
                                navController.navigate(Route.SetName.route)
                            }

                            MePropertyItem(
                                label = stringResource(id = R.string.birthday),
                                value = SimpleDateFormat("yyyy-MM-dd").format(
                                    Date(birth)
                                ),
                            ) {
                            }

                            MePropertyItem(
                                label = stringResource(id = R.string.account),
                                value = me!!.userID,
                                arrow = false,
                            ) {
                                navController.navigate(Route.SetName.route)
                            }

//                            MePropertyItem(
//                                label = stringResource(id = R.string.email),
//                                value = email,
//                            ) {
//                                navController.navigate(Route.SetEmail.route)
//                            }
//                            MeDividerItem()

                        }
                    }
                }
            }
        })
}


@Composable
fun MePropertyFace(
    faceURL: String,
    click: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .background(color = Color.White)
            .clickable {
                click()
            }) {

        val (title, faceView, rightArrow, divider) = createRefs()
        Text(
            text = stringResource(id = R.string.faceurl),
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top, margin = 0.dp)
                    bottom.linkTo(parent.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                }
        )

        AsyncImage(
            model = faceURL,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(faceView) {
                    top.linkTo(parent.top, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                    end.linkTo(rightArrow.start, margin = 6.dp)
                }
                .size(64.dp)
                .clip(
                    RoundedCornerShape(4.dp)
                )
        )

        IconButton(
            onClick = { click() },
            modifier = Modifier.constrainAs(rightArrow) {
                top.linkTo(parent.top, margin = 8.dp)
                bottom.linkTo(parent.bottom, margin = 8.dp)
                end.linkTo(parent.end, margin = 14.dp)
            }
        ) {
            Image(mipmap = R.mipmap.right_arrow)
        }


        Divider(color = Color(0xFFEBEBEB),
            modifier = Modifier
                .constrainAs(divider) {
                    bottom.linkTo(parent.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end)
                }, thickness = 0.5.dp)
    }
}

@Composable
fun MePropertyItem(
    label: String,
    value: String,
    arrow: Boolean = true,
    click: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .background(color = Color.White)
            .clickable {
                click()
            }) {

        val (title, summary, rightIcon, divider) = createRefs()
        Text(
            text = label,
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(parent.top, margin = 0.dp)
                    bottom.linkTo(parent.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                }
        )

        if (arrow) {
            Text(
                text = value,
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier
                    .constrainAs(summary) {
                        top.linkTo(parent.top, margin = 0.dp)
                        bottom.linkTo(parent.bottom, margin = 0.dp)
                        end.linkTo(rightIcon.start, margin = 6.dp)
                    }
            )
            IconButton(
                onClick = { click() },
                modifier = Modifier.constrainAs(rightIcon) {
                    top.linkTo(parent.top, margin = 8.dp)
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                    end.linkTo(parent.end, margin = 14.dp)
                }
            ) {
                Image(mipmap = R.mipmap.right_arrow)
            }
        } else {
            Text(
                text = value,
                color = Color.Black,
                fontSize = 16.sp,
                modifier = Modifier
                    .constrainAs(summary) {
                        top.linkTo(parent.top, margin = 0.dp)
                        bottom.linkTo(parent.bottom, margin = 0.dp)
                        end.linkTo(parent.end, margin = 14.dp)
                    }
                    .widthIn(0.dp, 280.dp)
            )
        }

        Divider(color = Color(0xFFEBEBEB),
            modifier = Modifier
                .constrainAs(divider) {
                    bottom.linkTo(parent.bottom, margin = 0.dp)
                    start.linkTo(parent.start, margin = 16.dp)
                    end.linkTo(parent.end)
                }, thickness = 0.5.dp)
    }
}