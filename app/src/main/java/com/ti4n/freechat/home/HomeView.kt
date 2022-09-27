package com.ti4n.freechat.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.ti4n.freechat.widget.Image
import com.ti4n.freechat.R
import com.ti4n.freechat.Route
import com.ti4n.freechat.util.aniComposable
import com.ti4n.freechat.util.noAniComposable
import com.ti4n.freechat.wallet.SendMoneyInputDetailView
import com.ti4n.freechat.wallet.SendMoneyView
import com.ti4n.freechat.wallet.TokenDetailView
import com.ti4n.freechat.wallet.WalletView

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeView() {
    val systemUiController = rememberSystemUiController()
    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    Scaffold(bottomBar = {
        AnimatedVisibility(
            visible = currentRoute == HomeTab.Chat.route || currentRoute == HomeTab.Square.route || currentRoute == HomeTab.Discover.route || currentRoute == HomeTab.Me.route,
            enter = slideInVertically {
                it
            },
            exit = slideOutVertically {
                it
            }
        ) {
            BottomNavigation(backgroundColor = Color(0xFFF0F0F0), elevation = 8.dp) {
                HomeTab.values().forEach {
                    BottomNavigationItem(
                        selected = currentRoute == it.route,
                        onClick = {
                            navController.navigate(it.route) {
                                navController.graph.startDestinationRoute?.let { screen_route ->
                                    popUpTo(screen_route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Image(
                                mipmap = when (it) {
                                    HomeTab.Chat -> if (currentRoute == it.route) R.mipmap.chat_sel else R.mipmap.chat_nor
                                    HomeTab.Square -> if (currentRoute == it.route) R.mipmap.square_sel else R.mipmap.square_nor
                                    HomeTab.Discover -> if (currentRoute == it.route) R.mipmap.find_sel else R.mipmap.find_nor
                                    HomeTab.Me -> if (currentRoute == it.route) R.mipmap.mine_sel else R.mipmap.mine_nor
                                }
                            )
                        },
                        label = {
                            Text(
                                text = when (it) {
                                    HomeTab.Chat -> stringResource(id = R.string.chat)
                                    HomeTab.Square -> stringResource(id = R.string.square)
                                    HomeTab.Discover -> stringResource(id = R.string.discover)
                                    HomeTab.Me -> stringResource(id = R.string.me)
                                },
                                fontSize = 10.sp,
                                color = Color(if (currentRoute == it.route) 0xFF7359F5 else 0xFF1A1A1A)
                            )
                        },
                        alwaysShowLabel = true
                    )
                }
            }
        }
    }, modifier = Modifier.navigationBarsPadding()) {
        AnimatedNavHost(
            navController,
            startDestination = HomeTab.Chat.route
        ) {
            noAniComposable(
                HomeTab.Chat.route
            ) { _ ->
                ChatListView(Modifier.padding(it))
            }
            noAniComposable(
                HomeTab.Square.route
            ) { _ ->
                SquareView(Modifier.padding(it), navController = navController)
            }
            noAniComposable(
                HomeTab.Discover.route
            ) { _ ->
                DiscoverView(Modifier.padding(it), navController = navController)
            }
            noAniComposable(
                HomeTab.Me.route
            ) { _ ->
                MeView(Modifier.padding(it), navController = navController)
            }
            aniComposable(Route.Wallet.route) { _ ->
                WalletView(navController)
            }
            aniComposable(Route.SendMoney.route) { _ ->
                SendMoneyView(navController)
            }
            aniComposable(Route.SendMoneyInputDetail.route) { _ ->
                val backStackEntry = remember {
                    navController.getBackStackEntry(Route.SendMoney.route)
                }
                SendMoneyInputDetailView(navController, hiltViewModel(backStackEntry))
            }
            aniComposable(Route.TokenDetail.route) { navBackEntry ->

//                TokenDetailView(navController, )
            }
        }
    }
}

enum class HomeTab(val route: String = "") {
    Chat("chat"), Square("square"), Discover("discover"), Me("me")
}