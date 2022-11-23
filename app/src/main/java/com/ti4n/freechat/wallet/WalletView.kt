package com.ti4n.freechat.wallet

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.model.response.freechat.ERC20Token
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.MiddleEllipsisText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletView(navController: NavController, viewModel: WalletViewModel = hiltViewModel()) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    val list = viewModel.list
    val address by viewModel.address.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White
            ), title = {
                HomeTitle(R.string.wallet)
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
                }
            })
        Spacer(modifier = Modifier.height(20.dp))
        WalletCard(address, list.sumOf { it.usd.toDouble() })
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            ItemFunction(
                image = R.mipmap.send,
                text = R.string.transfer,
                click = { navController.navigate(Route.SendMoney.route) })
            Spacer(modifier = Modifier.weight(1f))
            ItemFunction(
                image = R.mipmap.receive,
                text = R.string.receive_money,
                click = { navController.navigate(Route.ReceiveMoney.route) }
            )
            Spacer(modifier = Modifier.weight(1f))
            ItemFunction(
                image = R.mipmap.swap,
                text = R.string.swap,
                click = { navController.navigate(Route.Swap.route) })
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.all_money),
            color = Color(0xFF1A1A1A),
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
            items(list) {
                ItemCoin(token = it.token, count = it.balance, usd = it.usd) {
                    viewModel.setSelectedToken(it.token)
                    navController.navigate(
                        Route.TokenDetailSimply.jump(
                            it.token.symbol, viewModel.address.value, it.token.contractAddress
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun WalletCard(address: String, allMoney: Double) {
    val clipboardManager = LocalClipboardManager.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Image(
            mipmap = R.mipmap.guzhi_bg,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        Column(modifier = Modifier.padding(24.dp)) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.total_asset),
                color = Color(0x99FFFFFF),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "$ ",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "$allMoney",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(36.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "FCID: ", color = Color(0x99FFFFFF), fontSize = 14.sp)
                MiddleEllipsisText(
                    text = address,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(mipmap = R.mipmap.copy, Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(address))
                })
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ItemCoin(token: ERC20Token, count: String, usd: String, click: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { click() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(model = token.LogoURI,
            contentDescription = null,
            modifier = Modifier.size(40.dp),
            onError = {
                it.result.throwable.printStackTrace()
            })
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                text = token.Name,
                color = Color(0xFF1A1A1A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = token.symbol,
                color = Color(0xFFB3B3B3),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Column(modifier = Modifier.padding(start = 8.dp), horizontalAlignment = Alignment.End) {
            Text(
                text = "${count.toBigDecimalOrNull()?.toPlainString()}",
                color = Color(0xFF1A1A1A),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "=$$usd",
                color = Color(0xFF1A1A1A),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ItemFunction(
    @DrawableRes image: Int,
    @StringRes text: Int,
    backgroundColor: Color = Color(0xFFF2F3FC),
    textColor: Color = Color(0xFF666666),
    click: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { click() }) {
        Box(
            modifier = Modifier
                .background(backgroundColor, CircleShape)
                .padding(12.dp)
        ) {
            Image(mipmap = image)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(id = text), color = textColor, fontSize = 12.sp
        )
    }
}