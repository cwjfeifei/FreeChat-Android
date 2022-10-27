package com.ti4n.freechat.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.ti4n.freechat.widget.ImageButton

@Composable
fun ConfirmTransactionView(navController: NavController, viewModel: SendMoneyViewModel) {
    val toToken by viewModel.selectedToken.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val amountUSD by viewModel.amountUSD.collectAsState()
    val fromAddress by viewModel.fromAddress.collectAsState()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = CenterHorizontally
    ) {
        TopAppBar(backgroundColor = Color.Transparent, title = {
            HomeTitle(R.string.confirm_transaction)
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        }, elevation = 0.dp)
        Spacer(modifier = Modifier.height(40.dp))
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
            color = Color(0xFF1A1A1A),
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$amount ${toToken?.symbol}",
            fontSize = 20.sp,
            color = Color(0xFF1A1A1A),
            fontWeight = FontWeight.Bold
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .height(1.dp)
                .background(
                    Color(0xFFDBDBDB)
                )
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                backgroundColor = Color(0xFFF7F7F7),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(124.dp),
                elevation = 0.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 20.dp)) {
                    Text(
                        text = stringResource(id = R.string.send_address),
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = fromAddress, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Image(mipmap = R.mipmap.jiantou, modifier = Modifier.padding(horizontal = 8.dp))
            Card(
                backgroundColor = Color(0xFFF7F7F7),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(124.dp),
                elevation = 0.dp
            ) {
                Column(
                    horizontalAlignment = End,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.receive_account),
                        fontSize = 16.sp,
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = toAddress, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.consume_gas),
                fontSize = 14.sp,
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = End) {
                Row {
                    Text(
                        text = "$${String.format("%.2f", usd)}",
                        fontSize = 14.sp,
                        color = Color(0xFF1B1B1B)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$eth ETH",
                        fontSize = 14.sp,
                        color = Color(0xFF1B1B1B),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(id = R.string.max_gas, "$maxEth ETH"),
                    fontSize = 14.sp,
                    color = Color(0xFF808080)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = Color(0xFFDBDBDB)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(Modifier.padding(horizontal = 16.dp)) {
            Column {
                Text(
                    text = stringResource(id = R.string.total_consume),
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = stringResource(id = R.string.amount_gas),
                    fontSize = 12.sp,
                    color = Color(0xFF1A1A1A)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "$${String.format("%.2f", usd + amountUSD)}",
                    fontSize = 14.sp,
                    color = Color(0xFF1B1B1B)
                )
                Text(
                    text = "$amount ${toToken?.symbol}+$eth ETH",
                    fontSize = 14.sp,
                    color = Color(0xFF1B1B1B),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(
                        id = R.string.max_consume,
                        "$amount ${toToken?.symbol}+$maxEth ETH"
                    ),
                    fontSize = 12.sp,
                    color = Color(0xFF808080)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 1.dp,
            color = Color(0xFFDBDBDB)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.weight(1f))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .padding(horizontal = 20.dp), Arrangement.SpaceBetween
        ) {
            ImageButton(title = R.string.back, mipmap = R.mipmap.return_btn) {
                navController.navigateUp()
            }
            ImageButton(
                title = R.string.transfer,
                mipmap = R.mipmap.next_btn,
                textColor = Color.White
            ) {
                viewModel.transfer()
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
                    Text(
                        text = stringResource(id = R.string.watch_detail),
                        color = Color(0xFF4B6AF7),
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                uriHandler.openUri("https://goerli.etherscan.io/tx/$transactionHash")
                            }
                            .padding(vertical = 12.dp),
                        style = TextStyle(textAlign = TextAlign.Center)
                    )
                }
            }
        }
    }
}