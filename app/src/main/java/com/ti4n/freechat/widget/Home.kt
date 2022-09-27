package com.ti4n.freechat.widget

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ti4n.freechat.R

@Composable
fun HomeTitle(@StringRes title: Int) {
    Text(
        text = stringResource(id = title),
        color = Color.Black,
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun HomeTitle(title: String) {
    Text(
        text = title,
        color = Color.Black,
        fontSize = 17.sp,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun SearchView(showSearchView: MutableState<Boolean>, searchText: MutableState<String>) {
    val (isShowSearchView, setShowSearchView) = showSearchView
    val (searchText, setSearchText) = searchText
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .height(32.dp)
                .weight(1f)
                .background(Color.White, RoundedCornerShape(4.dp))
                .clickable {
                    setShowSearchView(true)
                }
        ) {
            Spacer(modifier = Modifier.width(6.dp))
            Image(mipmap = R.mipmap.search)
            Spacer(modifier = Modifier.width(6.dp))
            if (!isShowSearchView) {
                Text(text = "搜索", fontSize = 14.sp, color = Color(0xFFB3B3B3))
            }
            AnimatedVisibility(
                visible = isShowSearchView,
                enter = expandHorizontally(),
                exit = shrinkHorizontally()
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { setSearchText(it) },
                    textStyle = TextStyle(fontSize = 14.sp, color = Color(0xFFB3B3B3)),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {

                    }),
                    decorationBox = {
                        Box(Modifier.weight(1f)) {
                            if (searchText.isEmpty() && isShowSearchView)
                                Text(text = "搜索", fontSize = 14.sp, color = Color(0xFFB3B3B3))
                            it()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    cursorBrush = SolidColor(Color(0xFF7359F5))
                )
            }
        }
        AnimatedVisibility(
            visible = isShowSearchView,
            enter = expandHorizontally(),
            exit = shrinkHorizontally()
        ) {
            Text(
                text = "取消",
                color = Color(0xFF7359F5),
                modifier = Modifier
                    .clickable { setShowSearchView(false) }
                    .padding(horizontal = 6.dp)
            )
        }
    }
}