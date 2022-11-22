package com.ti4n.freechat.wallet

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.model.response.freechat.ERC20Tokens
import com.ti4n.freechat.widget.CustomPaddingTextField
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image

@OptIn(ExperimentalMaterial3Api::class)
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
    navController.currentBackStackEntry
        ?.savedStateHandle?.getStateFlow<String?>("transferToken", "")
        ?.collectAsState()?.value?.let { result ->
            tokens.find { it.symbol == result }?.let {
                viewModel.setSelectedToken(it)
            }
        }
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
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
                HomeTitle(R.string.transfer)
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable {
                                navController.navigate(
                                    Route.SelectTransferToken.jump(
                                        ERC20Tokens(
                                            tokens
                                        ), selectedToken?.symbol ?: ""
                                    )
                                )
                            }
                            .background(Color(0x2BF5F5F5), RoundedCornerShape(4.dp))
                            .padding(vertical = 5.dp, horizontal = 16.dp)
                    ) {
                        AsyncImage(
                            model = selectedToken?.LogoURI,
                            contentDescription = null,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = (selectedToken?.symbol ?: "") + " " + (selectedToken?.Name
                                ?: ""),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(mipmap = R.mipmap.arrow_right)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Card(
            shape = RoundedCornerShape(8.dp),
            backgroundColor = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE6E6E6)),
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp)) {
                Text(
                    text = stringResource(id = R.string.amount),
                    color = Color(0xFF1A1A1A), fontWeight = FontWeight.Bold, fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                CustomPaddingTextField(
                    value = amount,
                    onValueChange = {
                        viewModel.setAmount(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
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
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 16.sp
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(color = Color(0xFF1A1A1A), fontSize = 16.sp),
                    trailingIcon = {
                        Text(
                            text = stringResource(id = R.string.all),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .background(Color(0xFF141B33), RoundedCornerShape(50))
                                .padding(vertical = 4.dp, horizontal = 16.dp)
                                .clickable { viewModel.setAmount(remainAmount) },
                            Color.White
                        )
                    },
                    padding = PaddingValues(vertical = 14.dp, horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = stringResource(id = R.string.balance_of, remainAmount), color = Color(0xFF1B1B1B))
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            TextButton(
                onClick = { navController.navigateUp() },
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 10.dp)
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
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier
                    .weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF3879FD), contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 10.dp)
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