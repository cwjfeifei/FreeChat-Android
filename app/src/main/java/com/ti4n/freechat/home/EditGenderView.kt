package com.ti4n.freechat.home

import com.ti4n.freechat.util.clickableSingle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.setting.LanguageItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGenderView(navController: NavController, viewModel: MeEditViewModel) {
    val systemUiController = rememberSystemUiController()
    val gender by viewModel.gender.collectAsState()
    var temGender by remember {
        mutableStateOf(gender)
    }
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF5F5F5)
        )
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xFFF5F5F5)
            ),
            modifier = Modifier.statusBarsPadding(),
            title = {
                Text(
                    text = stringResource(id = R.string.select_gender),
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
                            viewModel.setGender(temGender)
                            navController.navigateUp()
                        }
                        .padding(horizontal = 16.dp))
            }
        )
    }, backgroundColor = Color.White) {
        Column(modifier = Modifier.padding(it)) {
            LanguageItem(
                title = stringResource(id = R.string.male), isSelected = temGender == 1
            ) {
                temGender = 1
            }
            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
            LanguageItem(
                title = stringResource(id = R.string.female), isSelected = temGender == 2
            ) {
                temGender = 2
            }
            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
            LanguageItem(
                title = stringResource(id = R.string.transgender), isSelected = temGender == 3
            ) {
                temGender = 3
            }
            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
        }
    }
}