package com.ti4n.freechat.login

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.ti4n.freechat.widget.Image

// Self Preview
@Composable
fun ProfilePreview(
    navController: NavController,
    userID: String
) {
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
                Image(
                    mipmap = R.mipmap.nav_back,
                    modifier = Modifier
                        .clickable { navController.navigateUp() }
                        .padding(vertical = 16.dp)
                )
            }

            androidx.compose.foundation.Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context)
                        .data(data = "https://freechat.world/images/face.apng")
                        .build(),
                    imageLoader = imageLoader,
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.FillBounds
            )

            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(color = Color.LightGray)
            ) {
            }
            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
            TextButton(
                onClick = {},
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