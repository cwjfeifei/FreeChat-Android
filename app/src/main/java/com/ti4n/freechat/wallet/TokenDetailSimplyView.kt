package com.ti4n.freechat.wallet

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Divider
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.model.response.Transaction
import com.ti4n.freechat.util.toWei
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import java.math.BigInteger
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TokenDetailSimplyView(
    navController: NavController,
    viewModel: WalletViewModel = hiltViewModel()
) {

    val token by viewModel.selectedToken.collectAsState()
    val transactions = viewModel.createPager().collectAsLazyPagingItems()
    val systemUiController = rememberSystemUiController()
    val address by viewModel.address.collectAsState()
    val tokenValue = viewModel.list.find { it.token.symbol == token?.symbol }
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFFF0F0F0)
        )
        systemUiController.setNavigationBarColor(Color.White)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFFF0F0F0)
            ), title = {
                HomeTitle(token?.Name ?: "")
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
                }
            }, actions = {
                Text(text = stringResource(id = R.string.detail),
                    Modifier
                        .clickable {
                            navController.navigate(
                                Route.TokenDetail.jump(
                                    token?.symbol ?: "",
                                    address
                                )
                            )
                        }
                        .padding(end = 16.dp), color = Color(0xFF333333))
            })
        Spacer(modifier = Modifier.height(40.dp))
        AsyncImage(
            model = token?.LogoURI,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .align(CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = tokenValue?.balance ?: "0",
            color = Color(0xFF1A1A1A),
            fontSize = 18.sp,
            modifier = Modifier.align(CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$${tokenValue?.usd ?: "0"}",
            color = Color(0xFF1A1A1A),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.all_record),
            color = Color(0xFF4B6AF7),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.Start)
        )
        Divider(thickness = 1.dp, color = Color.Black.copy(alpha = 0.1f))
        if (transactions.itemCount == 0) {
            Spacer(modifier = Modifier.weight(1f))
            Image(mipmap = R.mipmap.no_data)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.no_data),
                color = Color(0xFF999999),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.weight(1f))
        } else {
            LazyColumn {
                items(transactions) {
                    it?.let {
                        ItemTransaction(
                            transaction = it,
                            address = address,
                            token?.Decimals ?: 18
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemTransaction(transaction: Transaction, address: String, decimal: Int) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(
                text = if (transaction.from == address) "付款" else "收款",
                color = Color(0xFF4D4D4D),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${
                    SimpleDateFormat.getDateTimeInstance()
                        .format(Date("${transaction.timeStamp}000".toLong()))
                }",
                color = Color(0xFF999999),
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = if (transaction.from == address) "-${BigInteger(transaction.value).toWei(decimal)}" else "+${
                BigInteger(
                    transaction.value
                ).toWei(decimal)
            }",
            color = Color(0xFF1A1A1A),
            fontSize = 14.sp
        )
    }
}