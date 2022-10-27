@file:OptIn(ExperimentalMaterialApi::class)

package com.ti4n.freechat.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.widget.*

@Composable
fun ChatListView(navController: NavController, modifier: Modifier = Modifier) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    val showSearchView = remember {
        mutableStateOf(false)
    }
    var showAddView by remember {
        mutableStateOf(false)
    }
    val searchText = remember {
        mutableStateOf("")
    }
    val scrollState = rememberLazyListState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF0F0F0))
    ) {
        AnimatedVisibility(
            visible = !showSearchView.value,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            TopAppBar(backgroundColor = Color(0xFFF0F0F0), title = {
                HomeTitle(R.string.app_name)
            }, actions = {
                Box {
                    Image(mipmap = R.mipmap.add, modifier = Modifier.clickable {
                        showAddView = true
                    })
                    DropdownMenu(
                        expanded = showAddView,
                        onDismissRequest = { showAddView = false },
                        modifier = Modifier.background(Color(0xFF4B4B4B)),
                        offset = DpOffset(0.dp, 12.dp)
                    ) {
                        DropdownMenuItem(onClick = { }) {
                            Text(text = "添加好友", fontSize = 16.sp, color = Color.White)
                        }
                        Divider(color = Color(0xFF5F5F5F))
                        DropdownMenuItem(onClick = { }) {
                            Text(text = "创建圈子", fontSize = 16.sp, color = Color.White)
                        }
                        Divider(color = Color(0xFF5F5F5F))
                        DropdownMenuItem(onClick = { }) {
                            Text(text = "钱包", fontSize = 16.sp, color = Color.White)
                        }
                        Divider(color = Color(0xFF5F5F5F))
                        DropdownMenuItem(onClick = { }) {
                            Text(text = "扫一扫", fontSize = 16.sp, color = Color.White)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(24.dp))
            }, elevation = 0.dp, modifier = Modifier.statusBarsPadding())
        }
        Spacer(modifier = Modifier.height(6.dp))
        SearchView(showSearchView = showSearchView, searchText = searchText)
        Spacer(modifier = Modifier.height(8.dp))
        if (showSearchView.value) {

        } else {
            LazyColumn(state = scrollState, modifier = Modifier.background(Color.White)) {
                items(1) {
                    ChatItem(scrollState) {
                        navController.navigate(Route.PrivateChat.route)
                    }
                    Box(
                        Modifier
                            .padding(horizontal = 24.dp)
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = Color(0xFFF0F0F0))
                    )
                }
            }
        }
    }
}

@Composable
fun ChatItem(scrollState: LazyListState, onClick: () -> Unit) {
    val revealState = rememberRevealState()
    if (scrollState.isScrollInProgress) {
        LaunchedEffect(Unit) {
            if (revealState.currentValue == RevealValue.FullyRevealedStart)
                revealState.reset()
        }
    }
    RevealSwipe(
        directions = setOf(RevealDirection.EndToStart),
        hiddenContentEnd = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(78.dp)
                    .background(Color(0xFF1E84EF))
            ) {
                Image(mipmap = R.mipmap.top)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(78.dp)
                    .background(Color(0xFFFB5251))
            ) {
                Image(mipmap = R.mipmap.delete)
            }
        },
        backgroundCardEndColor = Color.White,
        backgroundCardContentColor = Color.White,
        animateBackgroundCardColor = false,
        state = revealState
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .background(Color.White)
                .clickable { onClick() }
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Image(mipmap = R.mipmap.logo, modifier = Modifier.size(44.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "落幕、sunshine",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hello",
                    fontSize = 12.sp,
                    color = Color(0xFFA2A6B2),
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "6月6日",
                fontSize = 12.sp,
                color = Color(0xFFCCCCCC),
                maxLines = 1
            )
        }
    }
}