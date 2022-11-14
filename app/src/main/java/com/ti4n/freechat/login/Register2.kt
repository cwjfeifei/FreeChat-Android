package com.ti4n.freechat.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Register2View(
    navController: NavController, type: LoginType, viewModel: RegisterViewModel = hiltViewModel()
) {
    val randomWords by viewModel.shuffledWord.collectAsState()
    val selectedWords by viewModel.clickedWords.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.shuffleWord()
    }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LoginCommonView(
            stringResource(
                id = when (type) {
                    LoginType.Login -> R.string.input_mnemonic
                    LoginType.Register -> R.string.input_mnemonic_by_order
                }
            ), backClick = { navController.navigateUp() }, next = when (type) {
                LoginType.Login -> R.string.login_freechat
                LoginType.Register -> R.string.next
            }, nextClick = {
                if (viewModel.canRegister()) {
                    navController.navigate(
                        Route.SetPassword.jump(
                            viewModel.words.value.joinToString(
                                " "
                            )
                        )
                    )
                } else {
                    Toast.makeText(context, "顺序不对", Toast.LENGTH_SHORT).show()
                }
            }, titleEndContent = {
                Chip(
                    onClick = { viewModel.resetWord() },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.height(28.dp),
                    colors = ChipDefaults.chipColors(
                        backgroundColor = Color(0xFFEFF1F5),
                        contentColor = Color(0xFF3879FD)
                    )
                ) {
                    Text(text = stringResource(id = R.string.reset))
                }
            }, tip = R.string.login_tip
        ) {
            Column {
                Spacer(Modifier.height(20.dp))
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 0.dp,
                    backgroundColor = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFE6E6E6))
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        userScrollEnabled = false,
                        modifier = Modifier
                            .height(92.dp)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        items(selectedWords) {
                            Box(contentAlignment = Alignment.Center,
                                modifier = Modifier.clickable { viewModel.deleteWord(it) }) {
                                Text(
                                    text = it,
                                    color = Color(0xFF1A1A1A),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(13.dp),
                    userScrollEnabled = false,
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth()
                ) {
                    items(randomWords) {
                        Chip(
                            onClick = { viewModel.addWord(it) },
                            enabled = !selectedWords.contains(it),
                            shape = RoundedCornerShape(4.dp),
                            colors = ChipDefaults.chipColors(
                                backgroundColor = Color(0xFFF4F6FA),
                                disabledBackgroundColor = Color(0xFFF4F6FA),
                                contentColor = Color(0xFF1A1A1A),
                                disabledContentColor = Color(0x801A1A1A)
                            )
                        ) {
                            Text(
                                text = it,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }

}