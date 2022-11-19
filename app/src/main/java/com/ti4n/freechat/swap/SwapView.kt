@file:OptIn(ExperimentalMaterialApi::class)

package com.ti4n.freechat.swap

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.model.response.freechat.ERC20Token
import com.ti4n.freechat.model.response.freechat.ERC20Tokens
import com.ti4n.freechat.wallet.InputPasswordBottomSheet
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.CustomPaddingTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwapView(navController: NavController, viewModel: SwapViewModel = hiltViewModel()) {
    val tokens by viewModel.supportTokens.collectAsState()
    val fromToken by viewModel.fromToken.collectAsState()
    val toToken by viewModel.toToken.collectAsState()
    val rate by viewModel.rate.collectAsState()
    val fromBalance by viewModel.fromBalance.collectAsState()
    val toBalance by viewModel.toBalance.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val toAmount by viewModel.toAmount.collectAsState()
    val gasUSD by viewModel.gasUSD.collectAsState()

    val showSuccessDialog by viewModel.transactionSend.collectAsState(initial = false)
    val transactionHash by viewModel.transactionHash.collectAsState()

    val quoteAmount by viewModel.quoteAmount.collectAsState()
    val impactValue by viewModel.impact.collectAsState()
    val uriHandler = LocalUriHandler.current
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = Color.Transparent)
        systemUiController.setNavigationBarColor(
            color = Color(0xFFF0F0F0)
        )
    }
    var showPassword by remember {
        mutableStateOf(false)
    }

    navController.currentBackStackEntry
        ?.savedStateHandle?.getStateFlow<String?>("swapFromToken", "")
        ?.collectAsState()?.value?.let { result ->
            tokens.find { it.symbol == result }?.let {
                viewModel.setFromToken(it)
            }
        }

    navController.currentBackStackEntry
        ?.savedStateHandle?.getStateFlow<String?>("swapToToken", "")
        ?.collectAsState()?.value?.let { result ->
            tokens.find { it.symbol == result }?.let {
                viewModel.setToToken(it)
            }
        }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            mipmap = R.mipmap.transfer_bg,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ), title = {
                    HomeTitle("闪兑")
                }, navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Image(mipmap = R.mipmap.nav_back)
                    }
                })
            Card(
                backgroundColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = CenterVertically,
                        ) {
                            AsyncImage(
                                model = fromToken?.LogoURI,
                                contentDescription = null,
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Column {
                                Row(verticalAlignment = CenterVertically) {
                                    Text(
                                        text = fromToken?.symbol ?: "",
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Image(mipmap = R.mipmap.push, Modifier.clickable {
                                        navController.navigate(
                                            Route.SelectSwapFromToken.jump(
                                                ERC20Tokens(
                                                    tokens.toMutableList().apply {
                                                        removeAll { it.symbol == toToken?.symbol }
                                                    }
                                                ),
                                                fromToken?.symbol ?: ""
                                            )
                                        )
                                    })
                                }
                                Text(
                                    text = "转出数量",
                                    color = Color(0xFF999999),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = fromBalance,
                            color = Color(0xFF4D4D4D),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        CustomPaddingTextField(
                            value = amount,
                            onValueChange = {
                                viewModel.setAmount(it)
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                textColor = Color(0xFF4D4D4D)
                            ),
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.input_amount),
                                    color = Color(0xFFB3B3B3)
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Image(
                        mipmap = R.mipmap.duihuan_icon,
                        modifier = Modifier.align(CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = CenterVertically) {
                            AsyncImage(
                                model = toToken?.LogoURI,
                                contentDescription = null,
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Column {
                                Row(verticalAlignment = CenterVertically) {
                                    Text(
                                        text = toToken?.symbol ?: "",
                                        color = Color(0xFF1A1A1A)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Image(mipmap = R.mipmap.push, Modifier.clickable {
                                        navController.navigate(
                                            Route.SelectSwapToToken.jump(
                                                ERC20Tokens(
                                                    tokens.toMutableList().apply {
                                                        removeAll { it.symbol == fromToken?.symbol }
                                                    }
                                                ),
                                                toToken?.symbol ?: ""
                                            )
                                        )
                                    })
                                }
                                Text(
                                    text = "转入数量",
                                    color = Color(0xFF999999),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = toBalance,
                            color = Color(0xFF4D4D4D),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        CustomPaddingTextField(
                            value = toAmount,
                            onValueChange = {
                                viewModel.setToAmount(it)
                            },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                textColor = Color(0xFF4D4D4D)
                            ),
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.input_amount),
                                    color = Color(0xFFB3B3B3)
                                )
                            },
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Decimal)
                        )
                    }
                }
            }
            Row(
                verticalAlignment = CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = "查看币种信息", color = Color(0xFF333333), fontSize = 12.sp)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color(0xFF333333))) {
                        append(fromToken?.symbol ?: "")
                        append("/")
                    }
                    withStyle(SpanStyle(color = Color(0xFF6F5FFC))) {
                        append(toToken?.symbol ?: "")
                    }
                })
            }
            Card(
                backgroundColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column {
                        Row(verticalAlignment = CenterVertically) {
                            AsyncImage(
                                model = toToken?.LogoURI,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = toToken?.symbol ?: "",
                                color = Color(0xFF1A1A1A)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = CenterVertically) {
                            Text(text = "汇率", color = Color(0xFF999999))
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "1${fromToken?.symbol ?: ""}=$rate ${toToken?.symbol ?: ""}",
                                color = Color(0xFF1A1A1A),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            ItemInfo(title = "预期获得", value = "$quoteAmount ${toToken?.symbol ?: ""}")
            ItemInfo(
                title = "兑换率影响",
                value = "${impactValue}%"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFE6E6E6), RoundedCornerShape(2.dp))
                    .padding(vertical = 4.dp)
            )
            ItemInfo(
                title = "收到的最低数额滑点后（0.05%）",
                value = "${(quoteAmount.toDoubleOrNull() ?: 0.0) * 0.95} ${toToken?.symbol ?: ""}"
            )

            ItemInfo(title = "网络费用", value = "$$gasUSD")
            Spacer(modifier = Modifier.height(8.dp))
            Spacer(modifier = Modifier.weight(1f))
            TextButton(
                onClick = {
                    showPassword = true
                },
                Modifier
                    .height(42.dp)
                    .fillMaxWidth(), shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.swap),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
    if (showSuccessDialog) {
        Dialog(onDismissRequest = { navController.navigateUp() }) {
            Column(Modifier.background(Color.White, RoundedCornerShape(10.dp))) {
                Spacer(modifier = Modifier.height(40.dp))
                Image(mipmap = R.mipmap.success, modifier = Modifier.align(CenterHorizontally))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.swap_finish),
                    color = Color(0xFF4B6AF7),
                    fontSize = 16.sp,
                    modifier = Modifier.align(CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(40.dp))
                Divider(color = Color(0xFFE6E6E6))
                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(id = R.string.finish_transaction),
                        color = Color.Black,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController.navigateUp() }
                            .padding(vertical = 12.dp),
                        style = TextStyle(textAlign = TextAlign.Center),
                    )
                    Spacer(
                        modifier = Modifier
                            .width(1.dp)
                            .height(50.dp)
                            .background(Color(0xFFE6E6E6))
                    )
                    Text(
                        text = stringResource(id = R.string.watch_detail),
                        color = Color(0xFF4B6AF7),
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                uriHandler.openUri("https://etherscan.io/tx/$transactionHash")
                            }
                            .padding(vertical = 12.dp),
                        style = TextStyle(textAlign = TextAlign.Center)
                    )
                }
            }
        }
    }
    if (showPassword)
        Dialog(onDismissRequest = { showPassword = false }) {
            InputPasswordBottomSheet(
                forgotPassword = { },
                confirm = {
                    showPassword = false
                    viewModel.swap(it)
                }
            )
        }
}

@Composable
fun ItemSupportToken(token: ERC20Token, click: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { click() }
            .padding(6.dp)
    ) {
        AsyncImage(
            model = token.LogoURI,
            contentDescription = null,
            modifier = Modifier.size(38.dp)
        )
        Spacer(modifier = Modifier.width(1.dp))
        Column {
            Row {
                Text(text = token.symbol, color = Color(0xFF1A1A1A))
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }
}

@Composable
fun ItemInfo(title: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 4.dp)
    ) {
        Text(text = title, color = Color(0xFF333333), fontSize = 12.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = value, color = Color(0xFF333333), fontSize = 12.sp)
    }
}