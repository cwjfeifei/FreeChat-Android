package com.ti4n.freechat.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordView(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var oldPassword by remember {
        mutableStateOf("")
    }
    var newPassword1 by remember {
        mutableStateOf("")
    }
    var newPassword2 by remember {
        mutableStateOf("")
    }
    Scaffold(topBar = {
        TopAppBar(
            backgroundColor = Color.White, elevation = 0.dp, modifier = Modifier.statusBarsPadding()
        ) {
            Text(text = stringResource(id = R.string.cancel),
                color = Color(0xFF181818),
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable {
                        navController.navigateUp()
                    }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.change_password),
                color = Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.done),
                fontSize = 14.sp,
                color = if (oldPassword.isNotEmpty() && newPassword1.isNotEmpty() && newPassword2.isNotEmpty()) Color(
                    0xFF26C24F
                ) else Color(0xFFB3B3B3),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable {
                        if (oldPassword.isNotEmpty() && newPassword1.isNotEmpty() && newPassword2.isNotEmpty()) {
                            scope.launch {
                                if (EthUtil.changeWalletPassword(
                                        context, oldPassword, newPassword1
                                    )
                                ) navController.navigateUp()
                            }
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 4.dp))
        }
    }, backgroundColor = Color.White) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color(0xFFF7F7F7)
                ),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.old_password),
                        color = Color(0xFFB3B3B3),
                        fontSize = 14.sp
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = newPassword1,
                onValueChange = { newPassword1 = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color(0xFFF7F7F7)
                ),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.new_password),
                        color = Color(0xFFB3B3B3),
                        fontSize = 14.sp
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextField(
                value = newPassword2,
                onValueChange = { newPassword2 = it },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color(0xFFF7F7F7)
                ),
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.confirm_again),
                        color = Color(0xFFB3B3B3),
                        fontSize = 14.sp
                    )
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.confirm_password_fit),
                color = Color(0xFF666666),
                fontSize = 12.sp
            )
        }
    }
}