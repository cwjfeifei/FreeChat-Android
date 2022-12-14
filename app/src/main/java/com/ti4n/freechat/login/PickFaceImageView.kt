package com.ti4n.freechat.login

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.ti4n.freechat.widget.DefaultProfileLargeImage
import com.ti4n.freechat.widget.Image
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// Select Face
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun PickFaceImageView(
    navController: NavController,
    viewModel: RegisterViewModel
) {
    val context = LocalContext.current

    val faceURL by viewModel.faceURL.collectAsState()
//    var gender by remember {
//        mutableStateOf(viewModel.gender.value)
//    }

    val faceUrls by viewModel.faceUrls.collectAsState()

    val imageLoader = ImageLoader.Builder(context).components {
        if (Build.VERSION.SDK_INT >= 28) {
            add(ImageDecoderDecoder.Factory())
        } else {
            add(GifDecoder.Factory())
        }
        add(AnimatedPngDecoder.Factory())
    }.build()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(Color.White)
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White
            ), title = {
                Text(
                    text = stringResource(id = R.string.set_faceurl),
                    color = Color(0xFF1A1A1A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
                }
            }, actions = {
                Chip(
                    onClick = {
                        faceURL?.let {
                            navController.navigate(
                                Route.ProfilePreview.jump(
                                    URLEncoder.encode(faceURL, StandardCharsets.UTF_8.toString()),
                                    viewModel.name.value.ifEmpty { context.getString(R.string.name) },
                                    viewModel.gender.value
                                )
                            )
                        }
                    },
                    border = BorderStroke(0.dp, Color.Transparent),
                    modifier = Modifier
                        .height(32.dp),
                    colors = ChipDefaults.chipColors(
                        backgroundColor = Color(0xFFF4F6FA), contentColor = Color(0xFF1A1A1A)
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.preview_profile),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
        if ((faceURL.ifEmpty { IM.DEFAULT_FACEURL }) == IM.DEFAULT_FACEURL)
            DefaultProfileLargeImage(isSelf = true)
        else
            androidx.compose.foundation.Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(context).data(data = faceURL)
                        .build(),
                    imageLoader = imageLoader,
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentScale = ContentScale.Crop
            )

//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//        ) {
//            GenderItem(
//                title = stringResource(id = R.string.male), isSelected = gender == 1
//            ) {
//                gender = 1
//            }
//            Spacer(modifier = Modifier.width(2.dp))
//            GenderItem(
//                title = stringResource(id = R.string.female), isSelected = gender == 2
//            ) {
//                gender = 2
//            }
//        }
//        Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 2.dp)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(faceUrls) {
                ItemFaceImage(it.small, it.small == faceURL) {
                    viewModel.setFaceURL(it.small)
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}

@Composable
fun LazyItemScope.ItemFaceImage(faceUrl: String, isSelected: Boolean, click: () -> Unit) {
    Box {
        AsyncImage(
            model = faceUrl, contentDescription = null,
            Modifier
                .fillParentMaxWidth(0.25f)
                .padding(horizontal = 4.dp)
                .aspectRatio(1f)
                .clip(
                    RoundedCornerShape(8.dp)
                )
                .clickableSingle { click() }
                .border(
                    1.dp,
                    if (isSelected) Color(0xFF3879FD) else Color(0xF5f5f5f5),
                    RoundedCornerShape(8.dp)
                ),
            contentScale = ContentScale.Crop
        )
        if (isSelected) {
            Image(
                mipmap = R.mipmap.icon_select_sel,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
            )
        }
    }
}

