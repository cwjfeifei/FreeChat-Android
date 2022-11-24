package com.ti4n.freechat.login

import androidx.compose.foundation.background
import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
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
import com.ti4n.freechat.home.DataTimePicker
import com.ti4n.freechat.util.getDayOfMonth
import com.ti4n.freechat.util.getMonthh
import com.ti4n.freechat.util.getYearr
import com.ti4n.freechat.widget.Image
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetBirthView(navController: NavController, viewModel: RegisterViewModel) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.White
        )
    }
//    var birth by remember {
//        mutableStateOf(viewModel.birth.value)
//    }
    val birth by viewModel.birth.collectAsState()
    val date = if (birth > 0) Date(birth) else Date()
    val selectYear = rememberSaveable { mutableStateOf(date.getYearr()) }
    val selectMonth = rememberSaveable { mutableStateOf(date.getMonthh()) }
    val selectDay = rememberSaveable { mutableStateOf(date.getDayOfMonth()) }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.White
            ),
            modifier = Modifier.statusBarsPadding(),
            title = {
                Text(
                    text = stringResource(id = R.string.set_birth),
                    color = Color.Black,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }, navigationIcon = {
                Text(text = stringResource(id = R.string.cancel),
                    color = Color(0xFF1B1B1B),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickableSingle {
                            navController.navigateUp()
                        }
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center)
            }, actions = {
                Text(text = stringResource(id = R.string.done),
                    fontSize = 14.sp,
                    color = Color(0xFF26C24F),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickableSingle {
                            viewModel.setBirth(selectYear.value, selectMonth.value, selectDay.value)
                            navController.navigateUp()
                        }
                        .padding(horizontal = 16.dp))
            }
        )
    }, backgroundColor = Color.White) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${selectYear.value}年${selectMonth.value}月${selectDay.value}日",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .background(Color(0xFFF7F7F7), RoundedCornerShape(8.dp))
                    .padding(vertical = 14.dp, horizontal = 20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.set_birth_tip),
                fontSize = 12.sp,
                color = Color(0xFF666666),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            DataTimePicker(selectYear, selectMonth, selectDay)
        }
    }
}