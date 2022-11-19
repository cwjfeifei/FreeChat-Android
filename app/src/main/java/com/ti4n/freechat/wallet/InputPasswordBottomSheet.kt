package com.ti4n.freechat.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ti4n.freechat.R
import com.ti4n.freechat.widget.CustomPaddingTextField
import com.ti4n.freechat.widget.Image
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun InputPasswordBottomSheet(
    forgotPassword: () -> Unit,
    confirm: (String) -> Unit
) {
    var password by remember {
        mutableStateOf("")
    }
    val focusManager = LocalFocusManager.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White, RoundedCornerShape(14.dp)
            )
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.wallet_password),
            color = Color(0xFF1A1A1A),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(20.dp))
        CustomPaddingTextField(
            value = password,
            onValueChange = { password = it },
            Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(44.dp),
            padding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                backgroundColor = Color(0xFFF5F5F5)
            ),
            textStyle = TextStyle(
                color = Color(0xFF1A1A1A),
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            ), shape = RoundedCornerShape(4.dp),
            placeholder = {
                Text(
                    text = stringResource(id = R.string.wallet_password_hint),
                    color = Color(0xFFB3B3B3),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.forget_password),
            color = Color(0xFF666666),
            fontSize = 14.sp,
            modifier = Modifier.clickable { forgotPassword() }
        )
        Spacer(modifier = Modifier.height(30.dp))
        Divider(color = Color(0xFFEBEBEB), thickness = 0.5.dp)
        TextButton(
            onClick = {
                focusManager.clearFocus()
                confirm(password)
            },
            Modifier
                .height(46.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = Color(0xFF4A84F7)
            )
        ) {
            Text(
                text = stringResource(id = R.string.confirm),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}