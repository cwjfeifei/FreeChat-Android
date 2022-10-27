package com.ti4n.freechat.wallet

import android.util.Log
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import androidx.compose.runtime.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import coil.request.ImageRequest
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.address
import org.kethereum.bip39.model.MnemonicWords

@Composable
fun WalletView(navController: NavController, viewModel: WalletViewModel = hiltViewModel()) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    val list = viewModel.list
    val address by viewModel.address.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        TopAppBar(backgroundColor = Color(0xFFF0F0F0), title = {
            HomeTitle(R.string.wallet)
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        })
        Spacer(modifier = Modifier.height(20.dp))
        WalletFunction(address,
            list.map { it.usd.toDouble() }.sum(),
            sendClick = { navController.navigate(Route.SendMoney.route) },
            receiveClick = { navController.navigate(Route.ReceiveMoney.route) },
            swapClick = { navController.navigate(Route.Swap.route) })
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
                            it.token.symbol, viewModel.address.value
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun WalletFunction(
    address: String,
    allMoney: Double,
    sendClick: () -> Unit = {},
    receiveClick: () -> Unit = {},
    swapClick: () -> Unit = {}
) {
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "FCID:$address", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(mipmap = R.mipmap.copy, Modifier.clickable {
                    clipboardManager.setText(AnnotatedString(address))
                })
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                    .background(Color(0x33364EA7), RoundedCornerShape(8.dp))
                    .padding(vertical = 12.dp)
            ) {
                Text(text = "资产总值:", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$$allMoney",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(46.dp)
            ) {
                ItemFunction(image = R.mipmap.send, text = R.string.transfer, click = sendClick)
                ItemFunction(
                    image = R.mipmap.receive, text = R.string.receive_money, click = receiveClick
                )
                ItemFunction(image = R.mipmap.swap, text = R.string.swap, click = swapClick)
            }
            Spacer(modifier = Modifier.height(20.dp))
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
    backgroundColor: Color = Color(0x1AFFFFFF),
    textColor: Color = Color.White,
    click: () -> Unit = {}
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { click() }) {
        Box(
            modifier = Modifier
                .background(backgroundColor, CircleShape)
                .padding(10.dp)
        ) {
            Image(mipmap = image)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = text), color = textColor, fontSize = 12.sp
        )
    }
}