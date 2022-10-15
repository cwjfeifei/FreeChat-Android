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
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.ImageButton

@Composable
fun SwapView(navController: NavController, viewModel: SwapViewModel = hiltViewModel()) {
    val tokens by viewModel.supportTokens.collectAsState()
    val fromToken by viewModel.fromToken.collectAsState()
    val toToken by viewModel.toToken.collectAsState()
    val rate by viewModel.rate.collectAsState()
    var fromExpanded by remember {
        mutableStateOf(false)
    }
    var toExpanded by remember {
        mutableStateOf(false)
    }
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = Color.Transparent)
        systemUiController.setNavigationBarColor(
            color = Color(0xFFF0F0F0)
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            mipmap = R.mipmap.transfer_bg,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            TopAppBar(backgroundColor = Color.Transparent, title = {
                HomeTitle("闪兑")
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                }
            }, elevation = 0.dp)
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
                        ExposedDropdownMenuBox(
                            expanded = fromExpanded,
                            onExpandedChange = { fromExpanded = !fromExpanded }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = fromToken?.LogoURI,
                                    contentDescription = null,
                                    modifier = Modifier.size(38.dp)
                                )
                                Spacer(modifier = Modifier.width(1.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = fromToken?.symbol ?: "",
                                            color = Color(0xFF1A1A1A)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Image(mipmap = R.mipmap.push, Modifier.clickable {
                                            fromExpanded = true
                                        })
                                    }
                                    Text(
                                        text = "转出数量",
                                        color = Color(0xFF999999),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = fromExpanded,
                                onDismissRequest = { fromExpanded = false }) {
                                tokens.forEach {
                                    ItemSupportToken(token = it) {
                                        viewModel.setFromToken(it)
                                        fromExpanded = false
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Image(mipmap = R.mipmap.duihuan_icon)
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        ExposedDropdownMenuBox(
                            expanded = toExpanded,
                            onExpandedChange = { toExpanded = !toExpanded }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(
                                    model = toToken?.LogoURI,
                                    contentDescription = null,
                                    modifier = Modifier.size(38.dp)
                                )
                                Spacer(modifier = Modifier.width(1.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = toToken?.symbol ?: "",
                                            color = Color(0xFF1A1A1A)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Image(mipmap = R.mipmap.push, Modifier.clickable {
                                            toExpanded = true
                                        })
                                    }
                                    Text(
                                        text = "转出数量",
                                        color = Color(0xFF999999),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = toExpanded,
                                onDismissRequest = { toExpanded = false }) {
                                tokens.forEach {
                                    ItemSupportToken(token = it) {
                                        viewModel.setToToken(it)
                                        toExpanded = false
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
            ItemInfo(title = "预期获得", value = "1000${toToken?.symbol ?: ""}")
            ItemInfo(title = "兑换率影响", value = "0.04%")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFFE6E6E6), RoundedCornerShape(2.dp))
                    .padding(vertical = 4.dp)
            )
            ItemInfo(title = "收到的最低数额滑点后（0.05%）", value = "1000${toToken?.symbol ?: ""}")
            ItemInfo(title = "网络费用", value = "1000")
            Spacer(modifier = Modifier.height(8.dp))
            ImageButton(
                title = R.string.swap,
                mipmap = R.mipmap.exchange_btn_nor,
                textColor = Color.White,
                modifier = Modifier.align(CenterHorizontally)
            ) {
                viewModel.swap("")
            }
        }
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