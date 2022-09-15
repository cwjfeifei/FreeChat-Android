package com.ti4n.freechat.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.ImageButton

@Composable
fun SetPasswordView(navController: NavController) {
    var password1 by remember {
        mutableStateOf("")
    }
    var password2 by remember {
        mutableStateOf("")
    }
    LoginCommonView("请设置您的临时密码") {
        Spacer(Modifier.height(60.dp))
        TextField(
            value = password1,
            onValueChange = { password1 = it },
            modifier = Modifier
                .fillMaxWidth(),
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(4.dp),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            placeholder = {
                Text(
                    text = "请输入您的临时密码",
                    color = Color(0xFF999999),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
        Spacer(Modifier.height(20.dp))
        TextField(
            value = password2,
            onValueChange = { password2 = it },
            modifier = Modifier
                .fillMaxWidth(),
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(4.dp),
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            ),
            placeholder = {
                Text(
                    text = "请确认您的临时密码",
                    color = Color(0xFF999999),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
        Spacer(Modifier.height(12.dp))
        Text(text = "确保您的密码有大写字母、小写字母等，位数大于8位。", color = Color(0xFF808080), fontSize = 12.sp)
        Spacer(Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth(),
            elevation = 4.dp
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "注意", color = Color(0xFF4D4D4D), fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "临时密码并不能找回您的账户，您依然需要助记词进行账户重置。再次提醒您请保护好您的助记词，助记词是唯一恢复您账户及资产的方式。",
                    color = Color(0xFF666666),
                    fontSize = 10.sp
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp), Arrangement.SpaceBetween
        ) {
            ImageButton(title = "返回", mipmap = R.mipmap.return_btn) {
                navController.navigateUp()
            }
            ImageButton(title = "下一步", mipmap = R.mipmap.next_btn) {
                if (password1 == password2)
                    navController.navigate(Route.Home.route)
            }
        }
    }
}