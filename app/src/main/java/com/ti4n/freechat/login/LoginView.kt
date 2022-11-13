package com.ti4n.freechat.login

import androidx.compose.foundation.lazy.grid.itemsIndexed
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.CustomPaddingTextField

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoginView(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val words = listOf(
        viewModel.word1,
        viewModel.word2,
        viewModel.word3,
        viewModel.word4,
        viewModel.word5,
        viewModel.word6,
        viewModel.word7,
        viewModel.word8,
        viewModel.word9,
        viewModel.word10,
        viewModel.word11,
        viewModel.word12
    )
    val context = LocalContext.current
    var preWords by remember {
        mutableStateOf(emptyList<String>())
    }
    var word by remember {
        mutableStateOf("")
    }
//    LaunchedEffect(key1 = word) {
//        preWords = if (word.isNotEmpty()) WORDLIST_ENGLISH.filter { it.startsWith(word) } else emptyList()
//    }
    LoginCommonView(
        stringResource(id = R.string.input_mnemonic),
        next = R.string.login_freechat,
        tip = R.string.login_tip,
        backClick = { navController.navigateUp() },
        nextClick = {
            if (viewModel.wordsIsCorrect()) {
                navController.navigate(Route.SetPassword.jump(words.joinToString(" ") { it.value }))
            } else {
                Toast.makeText(
                    context,
                    R.string.wrong_mnemonic,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, titleEndContent = {
            Chip(
                onClick = { words.forEach { it.component2()("") } },
                shape = RoundedCornerShape(0.5f),
                modifier = Modifier.height(28.dp),
                colors = ChipDefaults.chipColors(
                    backgroundColor = Color(0xFFEFF1F5),
                    contentColor = Color(0xFF3879FD)
                )
            ) {
                Text(text = stringResource(id = R.string.reset))
            }
        }
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            itemsIndexed(words) { index, item ->
                val (itemWord, wordChange) = item
                word = itemWord
                ItemInputWord(
                    index + 1,
                    word,
                    preWords,
                    wordChange
                )
            }
        }
    }
}

@Composable
fun ItemInputWord(index: Int, word: String, preWords: List<String>, wordChange: (String) -> Unit) {
    var exp by remember { mutableStateOf(preWords.isNotEmpty()) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "0$index".takeLast(2), color = Color(0xFF1A1A1A), fontSize = 14.sp)
        Spacer(modifier = Modifier.width(6.dp))

        CustomPaddingTextField(
            value = word,
            onValueChange = wordChange,
            singleLine = true,
            modifier = Modifier
                .onFocusChanged {
                    exp = it.hasFocus
                }
                .height(32.dp)
                .border(1.dp, Color(0xFFE6E6E6), RoundedCornerShape(4.dp)),
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            padding = PaddingValues(vertical = 8.dp, horizontal = 2.dp),
            shape = RoundedCornerShape(4.dp),
            textStyle = TextStyle(
                fontSize = 12.sp,
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center
            )
        )
    }
}