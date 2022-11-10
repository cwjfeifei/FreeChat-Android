package com.ti4n.freechat.login

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route

@Composable
fun Register2View(
    navController: NavController, type: LoginType, viewModel: RegisterViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val randomWords by viewModel.shuffledWord.collectAsState()
    val selectedWords by viewModel.clickedWords.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        viewModel.shuffleWord()
    }
    LoginCommonView(when (type) {
        LoginType.Login -> R.string.input_mnemonic
        LoginType.Register -> R.string.input_mnemonic_by_order
    }, backClick = { navController.navigateUp() }, next = when (type) {
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
    }) {
        Column(Modifier.verticalScroll(scrollState)) {
            Spacer(Modifier.height(20.dp))
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                backgroundColor = Color.White
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    userScrollEnabled = false,
                    modifier = Modifier
                        .height(176.dp)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    items(selectedWords) {
                        Card(
                            backgroundColor = Color.White,
                            shape = RoundedCornerShape(8.dp),
                            elevation = 0.dp,
                            border = BorderStroke(1.dp, Color(0xFFE6E6E6))
                        ) {
                            Box(contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .height(30.dp)
                                    .clickable { viewModel.deleteWord(it) }) {
                                Text(
                                    text = it,
                                    color = Color(0xFF4B6AF7),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                userScrollEnabled = false,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth()
            ) {
                items(randomWords) {
                    Card(
                        backgroundColor = Color(0xFFF4F6FA),
                        shape = RoundedCornerShape(8.dp),
                        elevation = 0.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(30.dp)
                                .clickable { viewModel.addWord(it) }) {
                            Text(
                                text = it,
                                color = Color(0xFF4D4D4D),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}