package com.ti4n.freechat.login

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ti4n.freechat.R
import com.ti4n.freechat.widget.InputCodeField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter

@Composable
fun VerifyEmailView(
    navController: NavController,
    userId: String,
    email: String,
    isRegister: Boolean,
    viewModel: RegisterViewModel
) {
    var verifyCode = ""
    LaunchedEffect(Unit) {
        viewModel.setEmailRoute.filter { it.isNotEmpty() }.collectLatest {
            navController.navigate(it)
        }
    }
    val sensoredEmail =
        email.split("@").mapIndexed { index, s -> if (index == 0) "***${s.takeLast(2)}" else s }
            .joinToString("@")

    LoginCommonView(title = stringResource(
        id = R.string.verify_email, sensoredEmail
    ),
        tip = R.string.verify_email_tip,
        backClick = { navController.navigateUp() },
        nextClick = {
            if (isRegister) viewModel.registerFreeChat(
                userId,
                verifyCode
            ) else viewModel.login(userId, verifyCode)
        }) {
        InputCodeField(
            finishInput = {
                verifyCode = it
                if (isRegister) viewModel.registerFreeChat(
                    userId,
                    verifyCode
                ) else viewModel.login(userId, verifyCode)
            }, modifier = Modifier.align(CenterHorizontally)
        )
    }
}