package com.ti4n.freechat.login

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ti4n.freechat.R
import com.ti4n.freechat.util.EmailAddressRegex
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

// 邮箱注册Freechat
@Composable
fun SetEmailView(
    navController: NavController,
    userID: String,
    viewModel: RegisterViewModel
) {
    var email by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var emailCheck by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        viewModel.setEmailRoute.filter { it.isNotEmpty() }.collectLatest {
            navController.navigate(it)
        }
    }
    LoginCommonView(
        stringResource(id = R.string.title_set_email),
        backClick = { navController.navigateUp() },
        next = R.string.next,
        nextClick = {
            if (!email.matches(EmailAddressRegex)) {
                emailCheck = context.getString(R.string.email_invalid)
            } else {
                scope.launch {
                    viewModel.setEmail(email)
                    viewModel.sendVerifyCode(userID)
                }
            }
        }, tip = R.string.email_tip
    ) {
        TextField(
            value = email,
            onValueChange = { email = it.trim() },
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
                    text = stringResource(id = R.string.set_email),
                    color = Color(0xFF999999),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = emailCheck,
            color = Color(0xFF808080),
            fontSize = 12.sp
        )
    }
}