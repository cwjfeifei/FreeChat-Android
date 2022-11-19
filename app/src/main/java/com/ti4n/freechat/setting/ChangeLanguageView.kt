package com.ti4n.freechat.setting

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.ti4n.freechat.widget.Image
import androidx.compose.runtime.*
import androidx.core.os.LocaleListCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ti4n.freechat.di.dataStore
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeLanguageView(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    var currentLanguage by remember {
        mutableStateOf("en")
    }
    LaunchedEffect(Unit) {
        currentLanguage = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    }
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF5F5F5)
        )
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(
                    0xFFF5F5F5
                )
            ), modifier = Modifier.statusBarsPadding(),
            title = {
                Text(
                    text = stringResource(id = R.string.language_setting),
                    color = Color.Black,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium
                )
            }, navigationIcon = {
                Text(text = stringResource(id = R.string.cancel),
                    color = Color(0xFF1B1B1B),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .clickable {
                            navController.navigateUp()
                        }
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center)
            }, actions = {
                Text(text = stringResource(id = R.string.done),
                    fontSize = 14.sp,
                    color = Color(0xFF26C24F),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable {
                            val appLocale =
                                LocaleListCompat.forLanguageTags(currentLanguage)
                            AppCompatDelegate.setApplicationLocales(appLocale)
                            navController.navigateUp()
                        }
                        .padding(horizontal = 16.dp, vertical = 4.dp))
            }
        )
    }, backgroundColor = Color.White) {
        Column(modifier = Modifier.padding(it)) {
            LanguageItem(
                title = stringResource(id = R.string.chinese),
                isSelected = currentLanguage == "zh"
            ) {
                currentLanguage = "zh"
            }
            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
            LanguageItem(
                title = stringResource(id = R.string.english),
                isSelected = currentLanguage == "en"
            ) {
                currentLanguage = "en"
            }
            Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp, startIndent = 16.dp)
        }
    }
}

@Composable
fun LanguageItem(title: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(16.dp)
    ) {
        Text(text = title, color = Color(0xFF1A1A1A), fontSize = 14.sp)
        Spacer(modifier = Modifier.weight(1f))
        if (isSelected) Image(mipmap = R.mipmap.select)
    }
}