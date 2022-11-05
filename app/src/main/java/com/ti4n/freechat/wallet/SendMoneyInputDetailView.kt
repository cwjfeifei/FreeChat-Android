package com.ti4n.freechat.wallet

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.widget.HomeTitle

@Composable
fun SendMoneyInputDetailView(
    navController: NavController,
    viewModel: SendMoneyViewModel = hiltViewModel()
) {
    val tokens by viewModel.tokens.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val remainAmount by viewModel.remainAmount.collectAsState()
    val selectedToken by viewModel.selectedToken.collectAsState()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        TopAppBar(backgroundColor = Color(0xFFF0F0F0), title = {
            HomeTitle(R.string.transfer)
        }, navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        })
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box(
                Modifier.background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF799EF9),
                            Color(0xFF425FF7),
                        )
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.token),
                        color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SelectToken(
                        list = tokens,
                        selectedToken = selectedToken,
                        onSelected = { viewModel.setSelectedToken(it) })
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color(0xFFCCCCCC)),
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.amount),
                    color = Color(0xFF1A1A1A), fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    value = amount,
                    onValueChange = {
                        viewModel.setAmount(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        backgroundColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(4.dp),
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.input_amount_hint),
                            color = Color(0xFF999999),
                            style = TextStyle(textAlign = TextAlign.Center),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(textAlign = TextAlign.Center, color = Color(0xFF1A1A1A)),
                    trailingIcon = {
                        Text(
                            text = stringResource(id = R.string.all),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(Color(0xFF6B8FF8), RoundedCornerShape(2.dp))
                                .padding(vertical = 2.dp, horizontal = 10.dp)
                                .clickable { viewModel.setAmount(remainAmount) },
                            Color.White
                        )
                    }
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "可用: $remainAmount", color = Color(0xFF1B1B1B))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { navController.navigateUp() },
                border = BorderStroke(2.dp, Color(0xFF3879FD)),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White, contentColor = Color(0xFF3879FD)
                )
            ) {
                Text(
                    text = stringResource(id = R.string.back),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            TextButton(
                onClick = {
                    if (amount <= remainAmount)
                        navController.navigate(Route.ConfirmTransaction.route)
                },
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(id = R.string.transfer),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SelectToken(
    list: List<ERC20Token>,
    selectedToken: ERC20Token?,
    onSelected: (ERC20Token) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .background(Color(0x2BF5F5F5), RoundedCornerShape(4.dp))
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
                color = Color.White,
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
                        .height(46.dp)
                        .background(Color(0x2BF5F5F5), RoundedCornerShape(4.dp))
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