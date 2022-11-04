package com.ti4n.freechat.login

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.datepicker.MaterialDatePicker
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.ImageButton
import com.ti4n.freechat.util.getActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

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
    val birth by viewModel.birth.collectAsState()
    val datePicker by lazy { MaterialDatePicker.Builder.datePicker().build() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )
            .padding(horizontal = 16.dp)
            .systemBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = stringResource(id = R.string.complete_profile_tip),
            fontSize = 14.sp,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(id = R.string.set_avatar),
            fontSize = 20.sp,
            color = Color(0xFF333333),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (faceURL != null) AsyncImage(model = faceURL,
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .clickable { controller.navigate(Route.ChooseImageSourceBottom.route) })
        else Image(mipmap = R.mipmap.pic_photo,
            modifier = Modifier
                .size(90.dp)
                .clickable { controller.navigate(Route.ChooseImageSourceBottom.route) })
        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = Color(0xFFE5E5E5))
        CompleteProfileItem(R.string.name, click = { controller.navigate(Route.SetName.route) }) {
            ProfileValueText(nickname)
        }
        Divider(color = Color(0xFFE5E5E5))
        CompleteProfileItem(R.string.gender, false) {
            Row {
                GenderItem(
                    title = stringResource(id = R.string.male), isSelected = gender == 0
                ) {
                    viewModel.setGender(0)
                }
                Spacer(modifier = Modifier.width(2.dp))
                GenderItem(
                    title = stringResource(id = R.string.female), isSelected = gender == 1
                ) {
                    viewModel.setGender(1)
                }
            }
        }
        Divider(color = Color(0xFFE5E5E5))
        CompleteProfileItem(R.string.birthday, click = {
            context.getActivity()?.supportFragmentManager?.let {
                datePicker.show(it, datePicker.toString())
                datePicker.addOnPositiveButtonClickListener {
                    viewModel.setBirthday(it)
                }
            }
        }) {
            ProfileValueText(SimpleDateFormat("yyyy.MM.dd").format(birth))
        }
        Divider(color = Color(0xFFE5E5E5))
        CompleteProfileItem(R.string.country) {
            ProfileValueText("中国")
        }
        Divider(color = Color(0xFFE5E5E5))
        CompleteProfileItem(R.string.region) {
            ProfileValueText("四川成都高新区")
        }
        Divider(color = Color(0xFFE5E5E5))
        Spacer(modifier = Modifier.weight(1f))
        ImageButton(
            title = R.string.complete_setting, mipmap = R.mipmap.login_btn, textColor = Color.White
        ) {
            scope.launch {
                // TODO reset email and ex property
                var result = IM.setUserInfo(nickname, faceURL, gender, birth, null, null)
                if (result is Unit) {
                    // success
                    controller.navigate(Route.Home.route)
                } else {
                }
//                viewModel.avatar.value?.let {
//                    Minio.uploadFile(context, it)?.let {
//                        val credentials = EthUtil.loadCredentials(context, "")
//                        viewModel.register(
//                            credentials.address, it, ""
//                        ) { controller.navigate(Route.Home.route) }
//                    }
//                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CompleteProfileItem(
    @StringRes title: Int,
    showArrow: Boolean = true,
    click: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { click() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(id = title), color = Color(0xFF333333), fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        content()
        if (showArrow) {
            Spacer(modifier = Modifier.width(8.dp))
            Image(mipmap = R.mipmap.right_arrow)
        }
    }
}

@Composable
fun ProfileValueText(value: String) {
    Text(text = value, color = Color(0xFF666666), fontSize = 16.sp)
}

@Composable
fun GenderItem(title: String, isSelected: Boolean, click: (String) -> Unit) {
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxHeight()
            .width(140.dp)
            .background(if (isSelected) Color(0xFF799AF9) else Color(0xFFF5F5F5))
            .clickable { click(title) }) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            color = if (isSelected) Color.White else Color(0xFF666666)
        )
    }
}