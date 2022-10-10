package com.ti4n.freechat.wallet

import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.model.response.Transaction
import com.ti4n.freechat.util.toWei
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import java.math.BigInteger
import java.util.Date

@Composable
fun TokenDetailSimplyView(
    navController: NavController,
    viewModel: TokenTransactionViewModel = hiltViewModel()
) {

    val token = viewModel.erc20Token
    val transactions = viewModel.pager.collectAsLazyPagingItems()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFFF0F0F0)
        )
        systemUiController.setNavigationBarColor(Color.White)
        Log.e("TokenDetailSimplyView", "TokenDetailSimplyView: $token")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(backgroundColor = Color(0xFFF0F0F0), title = {
            HomeTitle(token?.Name ?: "", Color(0xFF4B6AF7))
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        })

        Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            ItemFunction(
                image = R.mipmap.zz,
                text = R.string.transfer,
                textColor = Color(0xFF1A1A1A),
                backgroundColor = Color(0xFFE6E6E6)
            )
            ItemFunction(
                image = R.mipmap.sk,
                text = R.string.receive_money,
                textColor = Color(0xFF1A1A1A),
                backgroundColor = Color(0xFFE6E6E6)
            )
            ItemFunction(
                image = R.mipmap.xq,
                text = R.string.detail,
                textColor = Color(0xFF1A1A1A),
                backgroundColor = Color(0xFFE6E6E6)
            ) {
                navController.navigate(
                    Route.TokenDetail.jump(
                        viewModel.erc20Token?.symbol ?: "",
                        viewModel.address
                    )
                )
            }
        }

        if (transactions.itemCount == 0) {
            Image(mipmap = R.mipmap.no_data)
        } else {
            LazyColumn {
                items(transactions) {
                    it?.let {
                        ItemTransaction(
                            transaction = it,
                            address = viewModel.address,
                            viewModel.erc20Token?.Decimals ?: 18
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