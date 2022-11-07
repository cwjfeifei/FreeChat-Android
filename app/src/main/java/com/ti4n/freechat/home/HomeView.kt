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
import androidx.compose.ui.platform.LocalContext
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
import com.ti4n.freechat.addfriend.AddFriendView
import com.ti4n.freechat.contact.NewContactView
import com.ti4n.freechat.db.UserBaseInfoDao
import com.ti4n.freechat.im.PrivateChatView
import com.ti4n.freechat.profile.ApproveFriendApplicationView
import com.ti4n.freechat.profile.ProfileView
import com.ti4n.freechat.profile.SendFriendApplicationView
import com.ti4n.freechat.profile.SetRemarkView
import com.ti4n.freechat.swap.SwapView
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.util.aniComposable
import com.ti4n.freechat.util.noAniComposable
import com.ti4n.freechat.wallet.ConfirmTransactionView
import com.ti4n.freechat.wallet.ReceiveMoneyView
import com.ti4n.freechat.wallet.SendMoneyInputDetailView
import com.ti4n.freechat.wallet.SendMoneyView
import com.ti4n.freechat.wallet.TokenDetailSimplyView
import com.ti4n.freechat.wallet.TokenDetailView
import com.ti4n.freechat.wallet.WalletView

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HomeView(userBaseInfoDao: UserBaseInfoDao) {
    val systemUiController = rememberSystemUiController()
    val navController = rememberAnimatedNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val unread by IM.totalUnreadCount.collectAsState()
    val context = LocalContext.current
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color(0xFFF0F0F0)
        )
    }
    LaunchedEffect(Unit) {
//        @see LoginViewMode.autoLogin(context)
//        userBaseInfoDao.getUserInfo().filterNotNull().collectLatest {
//            IM.logout()
//            IM.login(it.userID, it.token)
//        }
    }
    Scaffold(bottomBar = {
        AnimatedVisibility(visible = currentRoute == HomeTab.Chat.route || currentRoute == HomeTab.Contact.route || currentRoute == HomeTab.Me.route,
            enter = slideInVertically {
                it
            },
            exit = slideOutVertically {
                it
            }) {
            BottomNavigation(backgroundColor = Color(0xFFF0F0F0), elevation = 8.dp) {
                HomeTab.values().forEach {
                    BottomNavigationItem(selected = currentRoute == it.route, onClick = {
                        navController.navigate(it.route) {
                            navController.graph.startDestinationRoute?.let { screen_route ->
                                popUpTo(screen_route) {
                                    saveState = true
                                }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }, icon = {
                        BadgedBox(badge = {
                            if (it == HomeTab.Chat && unread != 0) {
                                Badge(
                                    backgroundColor = Color(0xFFE64940),
                                    contentColor = Color.White
                                ) {
                                    Text(text = "$unread")
                                }
                            }
                        }) {
                            Image(
                                mipmap = when (it) {
                                    HomeTab.Chat -> if (currentRoute == it.route) R.mipmap.chat_sel else R.mipmap.chat_nor
                                    HomeTab.Contact -> if (currentRoute == it.route) R.mipmap.address_sel else R.mipmap.address_nor
//                                    HomeTab.Square -> if (currentRoute == it.route) R.mipmap.square_sel else R.mipmap.square_nor
//                                    HomeTab.Discover -> if (currentRoute == it.route) R.mipmap.find_sel else R.mipmap.find_nor
                                    HomeTab.Me -> if (currentRoute == it.route) R.mipmap.mine_sel else R.mipmap.mine_nor
                                }
                            )
                        }
                    }, label = {
                        Text(
                            text = when (it) {
                                HomeTab.Chat -> stringResource(id = R.string.chat)
                                HomeTab.Contact -> stringResource(id = R.string.contact)
//                                HomeTab.Square -> stringResource(id = R.string.square)
//                                HomeTab.Discover -> stringResource(id = R.string.discover)
                                HomeTab.Me -> stringResource(id = R.string.me)
                            },
                            fontSize = 10.sp,
                            color = Color(if (currentRoute == it.route) 0xFF4B6AF7 else 0xFF1A1A1A)
                        )
                    }, alwaysShowLabel = true
                    )
                }
            }
        }
    }, modifier = Modifier.navigationBarsPadding()) {
        AnimatedNavHost(
            navController, startDestination = HomeTab.Chat.route
        ) {
            noAniComposable(
                HomeTab.Chat.route
            ) { _ ->
                ChatListView(navController, Modifier.padding(it))
            }
            noAniComposable(
                HomeTab.Contact.route
            ) { _ ->
                ContactView(navController = navController)
            }
//            noAniComposable(
//                HomeTab.Square.route
//            ) { _ ->
//                SquareView(Modifier.padding(it), navController = navController)
//            }
//            noAniComposable(
//                HomeTab.Discover.route
//            ) { _ ->
//                DiscoverView(Modifier.padding(it), navController = navController)
//            }
            noAniComposable(HomeTab.Me.route) { _ ->
                MeView(Modifier.padding(it), navController = navController)
            }
            aniComposable(Route.MeDetails.route) { _ ->
                MeEditView(navController = navController)
            }
            aniComposable(Route.Wallet.route) { _ ->
                WalletView(navController)
            }
            aniComposable(Route.SendMoney.route) { _ ->
                SendMoneyView(navController)
            }
            aniComposable(Route.ReceiveMoney.route) { _ ->
                ReceiveMoneyView(navController)
            }
            aniComposable(Route.SendMoneyInputDetail.route) { _ ->
                val backStackEntry = remember {
                    navController.getBackStackEntry(Route.SendMoney.route)
                }
                SendMoneyInputDetailView(navController, hiltViewModel(backStackEntry))
            }
            aniComposable(Route.TokenDetail.route) { navBackEntry ->
                val backStackEntry = remember {
                    navController.getBackStackEntry(Route.Wallet.route)
                }
                TokenDetailView(
                    navController, hiltViewModel(backStackEntry)
                )
            }
            aniComposable(Route.TokenDetailSimply.route) { navBackEntry ->
                val backStackEntry = remember {
                    navController.getBackStackEntry(Route.Wallet.route)
                }
                TokenDetailSimplyView(
                    navController, hiltViewModel(backStackEntry)
                )
            }
            aniComposable(Route.Swap.route) { _ ->
                SwapView(
                    navController
                )
            }
            aniComposable(Route.ConfirmTransaction.route) { navBackEntry ->
                val backStackEntry = remember {
                    navController.getBackStackEntry(Route.SendMoney.route)
                }
                ConfirmTransactionView(
                    navController = navController, viewModel = hiltViewModel(backStackEntry)
                )
            }
            aniComposable(Route.PrivateChat.route) { _ ->
                PrivateChatView(
                    navController
                )
            }
            aniComposable(
                route = Route.Profile.route
            ) {
                ProfileView(navController = navController)
            }
            aniComposable(
                route = Route.LookFriendApplication.route
            ) {
                ProfileView(navController = navController, isFromFriendApplication = true)
            }
            aniComposable(
                route = Route.ApproveFriendApplication.route
            ) {
                val backStackEntry = remember {
                    navController.getBackStackEntry(Route.LookFriendApplication.route)
                }
                ApproveFriendApplicationView(
                    navController = navController,
                    hiltViewModel(backStackEntry)
                )
            }
            aniComposable(
                route = Route.SetRemark.route
            ) {
                val backStackEntry = remember {
                    navController.getBackStackEntry(Route.Profile.route)
                }
                SetRemarkView(navController = navController, hiltViewModel(backStackEntry))
            }
            aniComposable(
                route = Route.SendFriendApplication.route
            ) {
                val backStackEntry = remember {
                    navController.getBackStackEntry(Route.Profile.route)
                }
                SendFriendApplicationView(
                    navController = navController,
                    hiltViewModel(backStackEntry)
                )
            }
            aniComposable(Route.NewContact.route) {
                NewContactView(navController = navController)
            }
            aniComposable(Route.AddFriend.route) {
                AddFriendView(navController = navController)
            }
        }
    }
}

enum class HomeTab(val route: String = "") {
    Chat("chat"), Contact("contact"),

    //    Square("square"), Discover("discover"),
    Me("me")
}