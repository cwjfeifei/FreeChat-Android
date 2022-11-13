package com.ti4n.freechat.login

import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
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
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.AnimatedPngDecoder
import com.ti4n.freechat.util.IM
import kotlinx.coroutines.launch

private const val TAG = "CompleteProfileView"

@Composable
fun CompleteProfileView(controller: NavController, viewModel: RegisterViewModel = hiltViewModel()) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }

    val faceURL by viewModel.faceURL.collectAsState()
    val nickname by viewModel.name.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val email by viewModel.email.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val imageLoader = ImageLoader.Builder(context).components {
        if (Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
        add(AnimatedPngDecoder.Factory())
    }.build()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )
            .systemBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(id = R.string.complete_profile_tip),
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.complete_profile_tip_1),
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(id = R.string.complete_profile_tip_2),
            fontSize = 14.sp,
            color = Color(0xFF666666),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = stringResource(id = R.string.set_avatar),
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))
        androidx.compose.foundation.Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(data = faceURL).build(),
                imageLoader = imageLoader
            ),
            contentDescription = null,
            modifier = Modifier.size(160.dp),
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { controller.navigate(Route.PickFaceImage.route) },
            border = BorderStroke(1.dp, Color(0xFFE6E6E6)),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
                .height(36.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White, contentColor = Color(0xFF1A1A1A)
            )
        ) {
            Text(
                text = stringResource(id = R.string.set_faceurl),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
        CompleteProfileItem(R.string.name, true, click = {
            controller.navigate(Route.SetName.route)
        }) {
            Text(
                text = nickname,
                modifier = Modifier.weight(1f),
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
        Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
        CompleteProfileItem(R.string.gender, false) {
            Row(modifier = Modifier.weight(1f)) {
                GenderItem(
                    title = stringResource(id = R.string.male), isSelected = gender == 1
                ) {
                    viewModel.setGender(1)
                }
                Spacer(modifier = Modifier.width(2.dp))
                GenderItem(
                    title = stringResource(id = R.string.female), isSelected = gender == 2
                ) {
                    viewModel.setGender(2)
                }
            }
        }
        Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
        Spacer(modifier = Modifier.height(40.dp))
        TextButton(
            onClick = {
                scope.launch {
                    // must be IM.login
                    val result = IM.setUserInfo(faceURL, nickname, gender, 0, email, null)
                    if (result is Unit) {
                        // success
                        controller.backQueue.clear()
                        controller.navigate(Route.Home.route)
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
                text = stringResource(id = R.string.complete_setting),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CompleteProfileItem(
    @StringRes title: Int,
    showArrow: Boolean = true,
    click: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .clickable { click() }
        .padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text = stringResource(id = title), color = Color(0xFF1A1A1A), fontSize = 16.sp)
        Spacer(modifier = Modifier.width(24.dp))
        content()
        if (showArrow) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(mipmap = R.mipmap.right_arrow)
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
fun ProfileValueText(value: String) {
    Text(text = value, color = Color(0xFF666666), fontSize = 16.sp)
}

@Composable
fun RowScope.GenderItem(title: String, isSelected: Boolean, click: (String) -> Unit) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .background(if (isSelected) Color(0xFFE8F3FF) else Color(0xFFF5F5F5))
            .clickable { click(title) }) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            color = if (isSelected) Color(0xFF3879FD) else Color(0xFF1A1A1A)
        )
    }
}