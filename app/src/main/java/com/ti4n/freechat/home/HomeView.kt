package com.ti4n.freechat.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.R

@Composable
fun HomeView(navController: NavController) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    var currentTab by remember {
        mutableStateOf(HomeTab.Chat)
    }
    Scaffold(bottomBar = {
        BottomNavigation(backgroundColor = Color(0xFFF0F0F0), elevation = 8.dp) {
            HomeTab.values().forEach {
                BottomNavigationItem(
                    selected = currentTab == it,
                    onClick = { currentTab = it },
                    icon = {
                        Image(
                            mipmap = when (it) {
                                HomeTab.Chat -> if (currentTab == it) R.mipmap.chat_sel else R.mipmap.chat_nor
                                HomeTab.Square -> if (currentTab == it) R.mipmap.square_sel else R.mipmap.square_nor
                                HomeTab.Discover -> if (currentTab == it) R.mipmap.find_sel else R.mipmap.find_nor
                                HomeTab.Me -> if (currentTab == it) R.mipmap.mine_sel else R.mipmap.mine_nor
                            }
                        )
                    },
                    label = {
                        Text(
                            text = when (it) {
                                HomeTab.Chat -> "聊天"
                                HomeTab.Square -> "广场"
                                HomeTab.Discover -> "发现"
                                HomeTab.Me -> "我"
                            },
                            fontSize = 10.sp,
                            color = Color(if (currentTab == it) 0xFF7359F5 else 0xFF1A1A1A)
                        )
                    },
                    alwaysShowLabel = true
                )
            }
        }
    }, modifier = Modifier.navigationBarsPadding()) {
        when (currentTab) {
            HomeTab.Chat -> ChatListView(modifier = Modifier.padding(it))
            HomeTab.Square -> SquareView(modifier = Modifier.padding(it), navController)
            HomeTab.Discover -> DiscoverView(Modifier.padding(it), navController)
            HomeTab.Me -> MeView(Modifier.padding(it), navController)
        }
    }
}

enum class HomeTab {
    Chat, Square, Discover, Me
}