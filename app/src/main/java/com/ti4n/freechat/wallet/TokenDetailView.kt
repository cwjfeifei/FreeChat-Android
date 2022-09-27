package com.ti4n.freechat.wallet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image

@Composable
fun TokenDetailView(navController: NavController, token: ERC20Token) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
    ) {
        TopAppBar(backgroundColor = Color(0xFFF0F0F0), title = {
            HomeTitle(token.Name)
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        })
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(vertical = 40.dp, horizontal = 20.dp)
        ) {
            item {
                ItemDetail(title = "项目信息", content = token.tokenName)
            }
            item {
                ItemDetail(title = "代币简称", content = token.symbol)
            }
            item {
                ItemDetail(title = "网站", content = token.website)
            }
            item {
                ItemDetail(title = "合约地址", content = token.contractAddress)
            }
            item {
                ItemDetail(title = "描述", content = token.description)
            }
            item {
                ItemDetail(title = "发行总量", content = token.totalSupply)
            }
            item {
                ItemDetail(title = "精度", content = "${token.Decimals}")
            }
            item {
                ItemDetail(title = "社区", content = token.bitcointalk)
            }
        }
        Row(modifier = Modifier.padding(20.dp)) {
            if (token.github.isNotEmpty()) {
                Image(mipmap = R.mipmap.github)
            }
            if (token.twitter.isNotEmpty()) {
                Image(mipmap = R.mipmap.twitter)
            }
            if (token.facebook.isNotEmpty()) {
                Image(mipmap = R.mipmap.facebook)
            }
            if (token.reddit.isNotEmpty()) {
                Image(mipmap = R.mipmap.reddit)
            }
        }
    }
}

@Composable
fun ItemDetail(title: String, content: String) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            color = Color(0xFF1A1A1A),
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}