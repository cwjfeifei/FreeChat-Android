package com.ti4n.freechat.login

import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.layout.*
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
import com.ti4n.freechat.home.MeEditViewModel
import com.ti4n.freechat.widget.Image

@Composable
fun EditEmailView(navController: NavController, viewModel: MeEditViewModel) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    var email by remember {
        mutableStateOf(viewModel.email.value)
    }
    Scaffold(topBar = {
        TopAppBar(
            backgroundColor = Color.White, elevation = 0.dp, modifier = Modifier.statusBarsPadding()
        ) {
            Text(text = stringResource(id = R.string.cancel),
                color = Color(0xFF181818),
                fontSize = 14.sp,
                modifier = Modifier
                    .clickableSingle {
                        navController.navigateUp()
                    }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.edit_email),
                color = Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.done),
                fontSize = 14.sp,
                color = if (email.isNotEmpty()) Color(0xFF26C24F) else Color(0xFFB3B3B3),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickableSingle {
                        if (email.isNotEmpty()) {
                            viewModel.setEmail(email)
                            navController.navigateUp()
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 4.dp))
        }
    }, backgroundColor = Color(0xFFF0F0F0)) {
        Column(Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = email,
                onValueChange = { email = it },
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth(),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color.White
                ),
                trailingIcon = {
                    if (email != "") Image(mipmap = R.mipmap.close,
                        modifier = Modifier.clickableSingle { email = "" })
                },
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.please_input_your_name),
                        color = Color(0xFFB3B3B3),
                        fontSize = 16.sp
                    )
                })
        }
    }
}