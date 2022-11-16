package com.ti4n.freechat.swap

import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.gson.Gson
import com.ti4n.freechat.R
import com.ti4n.freechat.model.response.freechat.ERC20Tokens
import com.ti4n.freechat.widget.HomeTitle
import com.ti4n.freechat.widget.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectTokenView(
    navController: NavController,
    erC20Tokens: ERC20Tokens,
    selected: String,
    fromType: String
) {
    val systemUiController = rememberSystemUiController()
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
                HomeTitle(R.string.select_token)
            }, navigationIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Image(mipmap = R.mipmap.nav_back)
                }
            })
        LazyColumn {
            items(erC20Tokens.result) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set(fromType, it.symbol)
                            navController.popBackStack()
                        }
                        .background(if (selected == it.symbol) Color(0xFFF0F0F0) else Color.White)
                        .padding(vertical = 12.dp, horizontal = 16.dp)
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

val SelectTokenType = object : NavType<ERC20Tokens>(
    isNullableAllowed = false
) {
    override fun put(bundle: Bundle, key: String, value: ERC20Tokens) {
        bundle.putParcelable(key, value)
    }

    override fun get(bundle: Bundle, key: String): ERC20Tokens {
        return bundle.getParcelable(key) as? ERC20Tokens ?: ERC20Tokens(emptyList())
    }

    override fun parseValue(value: String): ERC20Tokens {
        return Gson().fromJson(value, ERC20Tokens::class.java)
    }
}