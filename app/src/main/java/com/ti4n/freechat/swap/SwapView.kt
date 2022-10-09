@file:OptIn(ExperimentalMaterialApi::class)

package com.ti4n.freechat.swap

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.model.response.swap.SupportToken
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image

@Composable
fun SwapView(navController: NavController, viewModel: SwapViewModel = hiltViewModel()) {
    val tokens by viewModel.supportTokens.collectAsState()
    val fromToken by viewModel.fromToken.collectAsState()
    val toToken by viewModel.toToken.collectAsState()
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
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                backgroundColor = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                ExposedDropdownMenuBox(
                    expanded = fromExpanded,
                    onExpandedChange = { fromExpanded = !fromExpanded }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = fromToken?.logoURI,
                            contentDescription = null,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.width(1.dp))
                        Column {
                            Row {
                                Text(text = fromToken?.symbol ?: "", color = Color(0xFF1A1A1A))
                                Spacer(modifier = Modifier.width(4.dp))
                                Image(mipmap = R.mipmap.push, Modifier.clickable {
                                    fromExpanded = true
                                })
                            }
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
        }
    }
}

@Composable
fun ItemSupportToken(token: SupportToken, click: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { click() }
            .padding(6.dp)
    ) {
        AsyncImage(
            model = token.logoURI,
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