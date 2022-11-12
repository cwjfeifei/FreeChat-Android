package com.ti4n.freechat.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import com.ti4n.freechat.R
import com.ti4n.freechat.widget.InputCodeField

@Composable
fun VerifyEmailView(navController: NavController) {

    var email by remember {
        mutableStateOf("")
    }

    LoginCommonView(
        title = R.string.verify_email,
        tip = R.string.verify_email_tip,
        backClick = { navController.navigateUp() },
        nextClick = { }) {
        InputCodeField(finishInput = {}, modifier = Modifier.align(CenterHorizontally))
    }
}