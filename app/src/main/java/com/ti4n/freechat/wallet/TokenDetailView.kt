package com.ti4n.freechat.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenDetailView(
    navController: NavController,
    viewModel: WalletViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val token by viewModel.selectedToken.collectAsState()
    val uriHandler = LocalUriHandler.current
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White
            ), title = {
                HomeTitle(token?.Name ?: "")
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
                }
            }
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(vertical = 40.dp, horizontal = 20.dp)
        ) {
            item {
                Row(Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    AsyncImage(
                        model = token?.LogoURI,
                        contentDescription = null,
                        Modifier
                            .padding(vertical = 24.dp)
                            .size(60.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            item {
                ItemDetail(title = "????????????", content = token?.tokenName)
            }
            item {
                ItemDetail(title = "????????????", content = token?.symbol)
            }
            item {
                ItemDetail(title = "??????", content = token?.website)
            }
            item {
                ItemDetail(title = "????????????", content = token?.contractAddress) {
                    Text(
                        text = stringResource(id = R.string.look_in_etherscan),
                        color = Color.White,
                        modifier = Modifier
                            .background(
                                Color(0xFF5C77E6), RoundedCornerShape(4.dp)
                            )
                            .clickableSingle {
                                uriHandler.openUri("https://etherscan.io/token/${token?.contractAddress}")
                            }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            item {
                ItemDetail(title = "??????", content = token?.description)
            }
            item {
                ItemDetail(title = "????????????", content = token?.totalSupply)
            }
            item {
                ItemDetail(title = "??????", content = "${token?.Decimals}")
            }
            item {
                ItemDetail(title = "??????", content = token?.bitcointalk)
            }
        }
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (!token?.github.isNullOrEmpty()) {
                Image(
                    mipmap = R.mipmap.github,
                    modifier = Modifier.clickableSingle { uriHandler.openUri(token!!.github) })
            }
            if (!token?.twitter.isNullOrEmpty()) {
                Image(
                    mipmap = R.mipmap.twitter,
                    modifier = Modifier.clickableSingle { uriHandler.openUri(token!!.twitter) })
            }
            if (!token?.facebook.isNullOrEmpty()) {
                Image(
                    mipmap = R.mipmap.facebook,
                    modifier = Modifier.clickableSingle { uriHandler.openUri(token!!.facebook) })
            }
            if (!token?.reddit.isNullOrEmpty()) {
                Image(
                    mipmap = R.mipmap.reddit,
                    modifier = Modifier.clickableSingle { uriHandler.openUri(token!!.reddit) })
            }
        }
    }
}

@Composable
fun ItemDetail(title: String, content: String?, addtion: @Composable () -> Unit = {}) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = content ?: "",
                color = Color(0xFF1A1A1A),
                fontSize = 14.sp,
                modifier = Modifier.weight(10f)
            )
            Spacer(modifier = Modifier.weight(1f))
            addtion()
        }
    }
}