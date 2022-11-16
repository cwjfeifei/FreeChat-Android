package com.ti4n.freechat.login

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
    val passedTime by viewModel.passedTime.collectAsState()
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
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
            onClick = { viewModel.resendCode(userId, email, isRegister) },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = Color(0xFF141B33),
                contentColor = Color.White,
                disabledContentColor = Color(0x80FFFFFF)
            ),
            enabled = passedTime >= 60
        ) {
            Text(
                text = if (passedTime >= 60) stringResource(id = R.string.send_verify_code) else stringResource(
                    id = R.string.resend_verify_code, "${60 - passedTime}"
                )
            )
        }
    }
}