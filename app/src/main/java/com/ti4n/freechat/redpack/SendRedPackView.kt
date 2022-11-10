package com.ti4n.freechat.redpack

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.wallet.SelectToken
import com.ti4n.freechat.wallet.SendMoneyViewModel
import com.ti4n.freechat.widget.CustomPaddingTextField
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image

@Composable
fun SendRedPackView(
    navController: NavController,
    viewModel: SendMoneyViewModel = hiltViewModel()
) {
    val tokens by viewModel.tokens.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val remainAmount by viewModel.remainAmount.collectAsState()
    val selectedToken by viewModel.selectedToken.collectAsState()
    val toToken by viewModel.selectedToken.collectAsState()
    val amountUSD by viewModel.amountUSD.collectAsState()
    val usd by viewModel.gasUSD.collectAsState()
    val eth by viewModel.gas.collectAsState()
    val maxEth by viewModel.maxGas.collectAsState()
    val systemUiController = rememberSystemUiController()
    val toUserInfo by viewModel.toUserInfo.collectAsState()

    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(backgroundColor = Color.Transparent, title = {
            HomeTitle("")
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Image(mipmap = R.mipmap.nav_back)
            }
        }, elevation = 0.dp)
        Spacer(modifier = Modifier.height(20.dp))
        AsyncImage(
            model = toUserInfo?.faceURL,
            contentDescription = "",
            modifier = Modifier
                .size(64.dp)
                .clip(
                    RoundedCornerShape(8.dp)
                )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = toUserInfo?.nickname ?: "",
                color = Color.Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(6.dp))
            Image(
                mipmap = if (toUserInfo?.gender == 1) R.mipmap.male else R.mipmap.female,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "FCID: ${toUserInfo?.userID ?: ""}",
            color = Color(0xFF808080),
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.select_transfer_token),
            color = Color(0xFF808080),
            fontSize = 12.sp,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(4.dp))
        SelectTokenRedPack(
            list = tokens,
            selectedToken = selectedToken,
            onSelected = { viewModel.setSelectedToken(it) })
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.select_transfer_amount),
            color = Color(0xFF808080),
            fontSize = 12.sp,
            modifier = Modifier
                .padding(start = 16.dp)
                .align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(4.dp))
        CustomPaddingTextField(
            value = amount,
            onValueChange = {
                viewModel.setAmount(it)
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(50.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color(0xFFF7F7F7)
            ),
            shape = RoundedCornerShape(8.dp),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.input_amount_hint),
                    color = Color(0xFFB3B3B3),
                    fontSize = 16.sp,
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            textStyle = TextStyle(
                textAlign = TextAlign.Start,
                color = Color(0xFF1A1A1A),
                fontSize = 16.sp
            ),
            padding = PaddingValues(horizontal = 20.dp, vertical = 14.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "可用: $remainAmount",
            color = Color(0xFF1A1A1A),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(start = 16.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
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
            Column(horizontalAlignment = Alignment.End) {
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
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
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
        TextButton(
            onClick = { navController.navigate(Route.ConfirmTransaction.route) },
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF3879FD), contentColor = Color.White
            )
        ) {
            Text(
                text = stringResource(id = R.string.next),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectTokenRedPack(
    list: List<ERC20Token>,
    selectedToken: ERC20Token?,
    onSelected: (ERC20Token) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color(0xFFF7F7F7), RoundedCornerShape(8.dp))
                .clickable {
                    expanded = true
                }
                .padding(vertical = 5.dp, horizontal = 16.dp)
        ) {
            AsyncImage(
                model = selectedToken?.LogoURI,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = (selectedToken?.symbol ?: "") + " " + (selectedToken?.Name ?: ""),
                color = Color(0xFF1A1A1A),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
        }
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(
                Color.White, RoundedCornerShape(8.dp)
            )
        ) {
            list.forEach {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color(0x2BF5F5F5), RoundedCornerShape(8.dp))
                        .clickable {
                            expanded = false
                            onSelected(it)
                        }
                        .padding(vertical = 5.dp, horizontal = 16.dp)
                ) {
                    AsyncImage(
                        model = it.LogoURI,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = it.symbol + " " + it.Name,
                        color = Color(0xFF1A1A1A),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}