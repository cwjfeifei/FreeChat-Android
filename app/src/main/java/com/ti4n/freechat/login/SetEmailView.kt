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
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.address
import com.ti4n.freechat.widget.ImageButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.kethereum.bip39.model.MnemonicWords

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
    var emailCheck = ""
    LaunchedEffect(Unit) {
//        viewModel.navigationRoute.filter { it.isNotEmpty() }.collectLatest {
//            navController.navigate(it)
//        }
    }
    LoginCommonView(R.string.title_set_email) {
        Spacer(Modifier.height(60.dp))
        TextField(
            value = email1,
            onValueChange = { email1 = it },
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
                    text = stringResource(id = R.string.set_email) ,
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
            onValueChange = { email2 = it },
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
                    text =stringResource(id = R.string.set_email2),
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
                    text = "账户密码并不能找回您的账户，您依然需要助记词进行账户重置。再次提醒您请保护好您的助记词，助记词是唯一恢复您账户及资产的方式。",
                    color = Color(0xFF666666),
                    fontSize = 10.sp
                )
            }
        }
        Spacer(Modifier.weight(1f))
        var context = LocalContext.current
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp), Arrangement.SpaceBetween
        ) {
            ImageButton(title = R.string.back, mipmap = R.mipmap.return_btn) {
                navController.navigateUp()
            }
            Spacer(Modifier.width(4.dp))
            ImageButton(
                title = R.string.next,
                mipmap = R.mipmap.next_btn,
                textColor = Color.White
            ) {
                if (email1 == email2) {
                    scope.launch {
//                        EthUtil.createWalletFile(context, password1, words)
                        // TODO register FreeChat account
                        viewModel.registerFreeChat(
                            context,
                            navController,
                            words = words,
                            email = email1,
                        )
                    }
                } else {
                    emailCheck = "输入的邮箱不一致"
                }
            }
        }
    }
}