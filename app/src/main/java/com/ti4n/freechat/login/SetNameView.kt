package com.ti4n.freechat.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.ti4n.freechat.widget.Image

@Composable
fun SetNameView(navController: NavController, viewModel: ProfileViewModel) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    var temName by remember {
        mutableStateOf(viewModel.name.value)
    }
    Scaffold(topBar = {
        TopAppBar(
            backgroundColor = Color(0xFFF0F0F0),
            contentPadding = PaddingValues(horizontal = 24.dp),
            elevation = 0.dp
        ) {
            Text(
                text = stringResource(id = R.string.cancel),
                color = Color(0xFF181818),
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .clickable {
                        navController.navigateUp()
                    },
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.set_name),
                color = Color.Black,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = R.string.done),
                fontSize = 14.sp,
                color = if (temName.isNotEmpty()) Color(0xFF1A1A1A) else Color(0xFFB1B1B1),
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(5.dp))
                    .clickable {
                        if (temName.isNotEmpty()) {
                            viewModel.setName(temName)
                            navController.navigateUp()
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }, backgroundColor = Color(0xFFF0F0F0)) {
        TextField(
            value = temName,
            onValueChange = { temName = it },
            modifier = Modifier
                .padding(it)
                .fillMaxWidth(),
            maxLines = 1,
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color.White
            ),
            trailingIcon = {
                if (temName != "")
                    Image(mipmap = R.mipmap.close, modifier = Modifier.clickable { temName = "" })
            },
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Black)
        )
    }
}