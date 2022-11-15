package com.ti4n.freechat.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.util.getDayOfMonth
import com.ti4n.freechat.util.getMonthh
import com.ti4n.freechat.util.getYearr
import com.ti4n.freechat.widget.Image
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditBirthView(navController: NavController, viewModel: MeEditViewModel) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
//    var birth by remember {
//        mutableStateOf(viewModel.birth.value)
//    }

    val date = if (viewModel.birth.value > 0) Date(viewModel.birth.value) else Date()
    val selectYear = rememberSaveable { mutableStateOf(date.getYearr()) }
    val selectMonth = rememberSaveable { mutableStateOf(date.getMonthh()) }
    val selectDay = rememberSaveable { mutableStateOf(date.getDayOfMonth()) }

    Scaffold(topBar = {
        TopAppBar(
            backgroundColor = Color.White, elevation = 0.dp, modifier = Modifier.statusBarsPadding()
        ) {
            Text(text = stringResource(id = R.string.cancel),
                color = Color(0xFF181818),
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable {
                        navController.navigateUp()
                    }
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.set_birth),
                color = Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = stringResource(id = R.string.done),
                fontSize = 14.sp,
                color = Color(0xFF26C24F),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable {
                        viewModel.setBirth(selectYear.value, selectMonth.value, selectDay.value)
                        navController.navigateUp()
                    }
                    .padding(horizontal = 16.dp, vertical = 4.dp))
        }
    }, backgroundColor = Color.White) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            TextField(
                value = "${selectYear.value}年${selectMonth.value}月${selectDay.value}日",
                onValueChange = { /**/ },
                modifier = Modifier
                    .padding(it)
                    .fillMaxWidth(),
                maxLines = 1,
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = Color(0xFFF7F7F7)
                ),
                textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.weight(1f))

            DataTimePicker(selectYear, selectMonth, selectDay, viewModel)
        }
    }
}