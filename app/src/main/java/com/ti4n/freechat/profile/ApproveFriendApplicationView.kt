package com.ti4n.freechat.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.widget.CustomPaddingTextField
import com.ti4n.freechat.widget.Image

@Composable
fun ApproveFriendApplicationView(navController: NavController, viewModel: ProfileViewModel) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    var remark by remember {
        mutableStateOf("")
    }
    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                Image(R.mipmap.nav_back,
                    modifier = Modifier
                        .clickable { navController.navigateUp() }
                        .padding(14.dp))
            },
            title = {
                Text(
                    text = stringResource(id = R.string.add_friend),
                    fontSize = 17.sp,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.SemiBold
                )
            },
            backgroundColor = Color.White, elevation = 0.dp,
            modifier = Modifier.statusBarsPadding()
        )
    }, backgroundColor = Color.White) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.set_remark),
                fontSize = 12.sp,
                color = Color(0xFF808080),
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            CustomPaddingTextField(value = remark,
                onValueChange = { remark = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(50.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color(0xFFF7F7F7)
                ),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                padding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.default_display_nickname),
                        fontSize = 16.sp,
                        color = Color(0xFFB3B3B3)
                    )
                },
                trailingIcon = {
                    Image(
                        mipmap = R.mipmap.close,
                        modifier = Modifier.clickable { remark = "" })
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    viewModel.acceptFriendApplication()
                    if (remark.isNotEmpty()) viewModel.setRemark(remark)
                    navController.navigateUp()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ),
                shape = RoundedCornerShape(0.dp)
            ) {
                Text(text = stringResource(id = R.string.finish), fontSize = 16.sp)
            }
        }
    }
}