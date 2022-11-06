package com.ti4n.freechat.login

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.AnimatedPngDecoder
import com.ti4n.freechat.widget.Image

// Select Face
@Composable
fun PickFaceImageView(
    navController: NavController,
    viewModel: PickFaceImageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val gender by viewModel.gender.collectAsState()

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
//                .align(Alignment.Start)
            )
            Text(
                text = stringResource(id = R.string.set_faceurl),
                color = Color(0xFF1A1A1A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
            )
            OutlinedButton(
                onClick = { navController.navigate(Route.ProfilePreview.route) },
                border = BorderStroke(0.dp, Color.Transparent),
                modifier = Modifier
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White, contentColor = Color(0xFF1A1A1A)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.preview_profile),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        androidx.compose.foundation.Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(data = "https://freechat.world/images/face.apng")
                    .build(),
//              ImageRequest.Builder(context).data(data = "https://pic-go-bed.oss-cn-beijing.aliyuncs.com/img/20220316151929.png")
//                    .build(),
                imageLoader = imageLoader,
            ),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Crop
        )

//        Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
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
        Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
        ) {
            // TODO face images list
        }
        Spacer(modifier = Modifier.height(8.dp))
    }

}