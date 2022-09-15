package com.ti4n.freechat.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.datepicker.MaterialDatePicker
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.ImageButton
import com.ti4n.freechat.util.getActivity
import java.text.SimpleDateFormat

@Composable
fun CompleteProfileView(controller: NavController, viewModel: ProfileViewModel = hiltViewModel()) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    val avatar by viewModel.avatar.collectAsState()
    val name by viewModel.name.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val birthday by viewModel.birthday.collectAsState()
    val country by viewModel.country.collectAsState()
    val location by viewModel.location.collectAsState()
    val datePicker by lazy { MaterialDatePicker.Builder.datePicker().build() }
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = "恭喜您\n已成功创建FreeChat账户 自由的世界还需一步\n下一步将会设置您的社交资料\n预计将耗时1分钟时间",
            fontSize = 14.sp,
            color = Color(0xFF333333),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "请设置您的头像",
            fontSize = 20.sp,
            color = Color(0xFF333333),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Image(
            mipmap = R.mipmap.pic_photo,
            modifier = Modifier
                .size(90.dp)
                .clickable { controller.navigate(Route.ChooseImageSourceBottom.route) })
        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = Color(0xFFE5E5E5))
        CompleteProfileItem("名字", click = { controller.navigate(Route.SetName.route) }) {
            ProfileValueText(name)
        }
        Divider(color = Color(0xFFE5E5E5))
        CompleteProfileItem("性别", false) {
            Row {
                GenderItem(title = "男", isSelected = gender == "男") {
                    viewModel.setGender(it)
                }
                Spacer(modifier = Modifier.width(2.dp))
                GenderItem(title = "女", isSelected = gender == "女") {
                    viewModel.setGender(it)
                }
            }
        }
        Divider(color = Color(0xFFE5E5E5))
        CompleteProfileItem("生日", click = {
            context.getActivity()?.supportFragmentManager?.let {
                datePicker.show(it, datePicker.toString())
                datePicker.addOnPositiveButtonClickListener {
                    viewModel.setBirthday(it)
                }
            }
        }) {
            ProfileValueText(SimpleDateFormat("yyyy.MM.dd").format(birthday))
        }
        Divider(color = Color(0xFFE5E5E5))
        CompleteProfileItem("国家") {
            ProfileValueText("中国")
        }
        Divider(color = Color(0xFFE5E5E5))
        CompleteProfileItem("地区") {
            ProfileValueText("四川成都高新区")
        }
        Divider(color = Color(0xFFE5E5E5))
        Spacer(modifier = Modifier.weight(1f))
        ImageButton(title = "完成设置", mipmap = R.mipmap.complete_btn) {
            controller.navigate(Route.Home.route)
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CompleteProfileItem(
    title: String,
    showArrow: Boolean = true,
    click: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable { click() }, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, color = Color(0xFF333333), fontSize = 16.sp)
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
    Box(contentAlignment = Alignment.Center, modifier = Modifier
        .fillMaxHeight()
        .width(140.dp)
        .background(if (isSelected) Color(0xFF968EFF).copy(0.8f) else Color(0xFFF5F5F5))
        .clickable { click(title) }) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            color = if (isSelected) Color.White else Color(0xFF666666)
        )
    }
}