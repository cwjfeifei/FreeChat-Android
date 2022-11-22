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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.address
import com.ti4n.freechat.widget.Image
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.kethereum.bip39.model.MnemonicWords

@Composable
fun SetPasswordView(
    navController: NavController, words: String, viewModel: SetPasswordViewModel = hiltViewModel()
) {
    var password1 by remember {
        mutableStateOf("")
    }
    var password2 by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.navigationRoute.filter { it.isNotEmpty() }.collectLatest {
            if (it == Route.Home.route) {
                navController.backQueue.clear()
            }
            navController.navigate(it)
        }
    }
    var showPassword1 by remember {
        mutableStateOf(false)
    }
    var showPassword2 by remember {
        mutableStateOf(false)
    }
    LoginCommonView(
        stringResource(id = R.string.set_password),
        backClick = { navController.navigateUp() },
        nextClick = {
            if (password1 == password2) {
                scope.launch {
                    EthUtil.createWalletFile(context, password1, words)
                    viewModel.onSetPassword(MnemonicWords(words).address().hex)
                }
            }
        },
        tip = R.string.password_tip
    ) {
        TextField(value = password1,
            onValueChange = { password1 = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(4.dp),
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF1A1A1A)),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.set_password),
                    color = Color(0xFFB3B3B3),
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            visualTransformation = if (showPassword1) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Next
            ),
            trailingIcon = {
                IconButton(onClick = { showPassword1 = !showPassword1 }) {
                    Image(mipmap = if (showPassword1) R.mipmap.password_show else R.mipmap.password_hide)
                }
            })
        Spacer(Modifier.height(20.dp))
        TextField(
            value = password2,
            onValueChange = { password2 = it },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color(0xFFF5F5F5)
            ),
            shape = RoundedCornerShape(4.dp),
            textStyle = TextStyle(
                fontSize = 16.sp, color = Color(0xFF1A1A1A)
            ),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.set_password2),
                    color = Color(0xFFB3B3B3),
                    fontSize = 16.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            visualTransformation = if (showPassword2) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
            ), trailingIcon = {
                IconButton(onClick = { showPassword2 = !showPassword2 }) {
                    Image(mipmap = if (showPassword2) R.mipmap.password_show else R.mipmap.password_hide)
                }
            }
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "确保您的密码有大写字母、小写字母等，位数大于8位。",
            color = Color(0xFF808080),
            fontSize = 12.sp
        )
    }
}