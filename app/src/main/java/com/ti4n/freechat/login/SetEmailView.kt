package com.ti4n.freechat.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
    words: String,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    var email1 by remember {
        mutableStateOf("")
    }
    var email2 by remember {
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
        R.string.title_set_email,
        backClick = { navController.navigateUp() },
        next = R.string.create_freechat,
        nextClick = {
            if (!email1.matches(EmailAddressRegex)) {
                emailCheck = context.getString(R.string.email_invalid)
            } else if (email1 == email2) {
                scope.launch {
                    viewModel.registerFreeChat(
                        context,
                        words = words,
                        email = email1,
                    )
                }
            } else {
                emailCheck = context.getString(R.string.set_email_not_same)
            }
        }, tip = R.string.email_tip
    ) {
        TextField(
            value = email1,
            onValueChange = { email1 = it.trim() },
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
            }
        )
        Spacer(Modifier.height(20.dp))
        TextField(
            value = email2,
            onValueChange = { email2 = it.trim() },
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
                    text = stringResource(id = R.string.set_email2),
                    color = Color(0xFF999999),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = emailCheck,
            color = Color(0xFF808080),
            fontSize = 12.sp
        )
    }
}