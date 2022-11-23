package com.ti4n.freechat.wallet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.MiddleEllipsisText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ConfirmTransactionView(
    navController: NavController, viewModel: SendMoneyViewModel, forgetPwd: () -> Unit
) {
    val toToken by viewModel.selectedToken.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val amountUSD by viewModel.amountUSD.collectAsState()
    val toAddress by viewModel.toAddress.collectAsState()
    val usd by viewModel.gasUSD.collectAsState()
    val eth by viewModel.gas.collectAsState()
    val maxEth by viewModel.maxGas.collectAsState()
    val showSuccessDialog by viewModel.transactionSend.collectAsState(initial = false)
    val transactionHash by viewModel.transactionHash.collectAsState()
    val uriHandler = LocalUriHandler.current
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    var showPassword by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        horizontalAlignment = CenterHorizontally
    ) {
        CenterAlignedTopAppBar(colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        ), title = {
            HomeTitle(R.string.confirm_transaction)
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Image(mipmap = R.mipmap.nav_back)
            }
        })
        Spacer(modifier = Modifier.height(16.dp))
        Box(Modifier.align(CenterHorizontally)) {
            Image(
                mipmap = R.mipmap.jiaoyi, modifier = Modifier
                    .offset(x = (-20).dp)
                    .size(60.dp)
            )
            AsyncImage(
                model = toToken?.LogoURI, null, modifier = Modifier
                    .offset(x = 20.dp)
                    .size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.transfer),
            fontSize = 20.sp,
            color = Color(0xFF9A9A9A),
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "$amount ${toToken?.symbol}",
            fontSize = 20.sp,
            color = Color(0xFF1B1B1B),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                backgroundColor = Color(0xFFF4F8FF),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                elevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp)) {
                    Text(
                        text = stringResource(id = R.string.send_address),
                        fontSize = 16.sp,
                        color = Color(0xFF999999),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MiddleEllipsisText(
                        text = viewModel.fromAddress,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = stringResource(id = R.string.current_account),
                        color = Color(0xFF656565),
                        fontSize = 14.sp
                    )
                }
            }
            Image(mipmap = R.mipmap.jiantou, modifier = Modifier.padding(horizontal = 8.dp))
            Card(
                backgroundColor = Color(0xFFF4F8FF),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                elevation = 0.dp
            ) {
                Column(
                    horizontalAlignment = End,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.receive_address),
                        fontSize = 14.sp,
                        color = Color(0xFF999999),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    MiddleEllipsisText(
                        text = toAddress,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "",
                        color = Color(0xFF656565),
                        fontSize = 14.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.consume_gas),
                    fontSize = 14.sp,
                    color = Color(0xFF999999),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(id = R.string.recommoned_site),
                    fontSize = 14.sp,
                    color = Color(0xFF999999)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(id = R.string.may_less_30),
                    fontSize = 12.sp,
                    color = Color(0xFF10C762)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = End) {
                Text(
                    text = "$${String.format("%.2f", usd)}",
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$eth ETH",
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.max_gas),
                    fontSize = 14.sp,
                    color = Color(0xFF999999)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$maxEth ETH",
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = Color(0xFFEBEBEB)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.total_consume),
                fontSize = 14.sp,
                color = Color(0xFF999999),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$${String.format("%.2f", usd + amountUSD)}",
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$amount ${toToken?.symbol}+$eth ETH",
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.amount_gas),
                fontSize = 14.sp,
                color = Color(0xFF999999),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.max_consume),
                fontSize = 14.sp,
                color = Color(0xFF999999),
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "$amount ${toToken?.symbol}+$maxEth ETH",
            fontSize = 14.sp,
            color = Color(0xFF1A1A1A),
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(End)
                .padding(end = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            TextButton(
                onClick = { navController.navigateUp() },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.back),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            TextButton(
                onClick = { showPassword = true },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ),
            ) {
                Text(
                    text = stringResource(id = R.string.confirm_and_broadcast),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    if (showPassword) Dialog(onDismissRequest = { showPassword = false }) {
        InputPasswordBottomSheet(forgotPassword = { }, confirm = {
            showPassword = false
            viewModel.transfer(it)
        }, close = { showPassword = false })
    }

    if (showSuccessDialog) {
        Dialog(onDismissRequest = { navController.navigateUp() }) {
            Column(Modifier.background(Color.White, RoundedCornerShape(10.dp))) {
                Spacer(modifier = Modifier.height(40.dp))
                Image(mipmap = R.mipmap.success, modifier = Modifier.align(CenterHorizontally))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = R.string.transaction_boardcasted),
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
                    Text(text = stringResource(id = R.string.watch_detail),
                        color = Color(0xFF4B6AF7),
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                uriHandler.openUri("https://etherscan.io/tx/$transactionHash")
                            }
                            .padding(vertical = 12.dp),
                        style = TextStyle(textAlign = TextAlign.Center))
                }
            }
        }
    }
}