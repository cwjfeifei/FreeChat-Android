package com.ti4n.freechat.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.address
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.ImageButton
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.kethereum.bip39.model.MnemonicWords
import org.web3j.crypto.MnemonicUtils
import org.web3j.crypto.WalletUtils
import java.io.File

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
            navController.navigate(it)
        }
    }
    LoginCommonView(R.string.set_password, backClick = { navController.navigateUp() }, nextClick = {
        if (password1 == password2 && password1.length >= 8) {
            scope.launch {
                EthUtil.createWalletFile(context, password1, words)

                viewModel.login(MnemonicWords(words).address().hex)
            }
        }
    }, tip = R.string.password_tip) {
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
            textStyle = TextStyle(
                fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Center
            ),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.set_password),
                    color = Color(0xFF999999),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            })
        Spacer(Modifier.height(20.dp))
        TextField(value = password2,
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
                fontSize = 16.sp, color = Color.Black, textAlign = TextAlign.Center
            ),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.set_password2),
                    color = Color(0xFF999999),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            })
        Spacer(Modifier.height(12.dp))
        Text(
            text = "确保您的密码有大写字母、小写字母等，位数大于8位。",
            color = Color(0xFF808080),
            fontSize = 12.sp
        )
    }
}