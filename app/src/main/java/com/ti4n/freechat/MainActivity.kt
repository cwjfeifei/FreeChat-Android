@file:OptIn(ExperimentalMaterialNavigationApi::class)

package com.ti4n.freechat

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ti4n.freechat.bottomsheet.ChooseImageSource
import com.ti4n.freechat.bottomsheet.VideoVoiceChat
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.home.HomeView
import com.ti4n.freechat.login.*
import com.ti4n.freechat.splash.NoInternetView
import com.ti4n.freechat.splash.PermissionView
import com.ti4n.freechat.splash.SplashView
import com.ti4n.freechat.ui.theme.FreeChatTheme
import com.ti4n.freechat.util.*
import com.ti4n.freechat.widget.BigImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var db: AppDataBase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        IM.init(this)
        setContent {
            FreeChatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val bottomSheetNavigator = rememberBottomSheetNavigator()
                    val navController = rememberAnimatedNavController(bottomSheetNavigator)
                    ModalBottomSheetLayout(
                        bottomSheetNavigator,
                        sheetBackgroundColor = Color.White,
                        sheetShape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    ) {
                        AnimatedNavHost(
                            navController = navController,
                            startDestination = Route.Splash.route
                        ) {
                            aniComposable(route = Route.Splash.route) {
                                SplashView(navController)
                            }
                            aniComposable(route = Route.MainLogin.route) {
                                MainLoginView(navController)
                            }
                            aniComposable(route = Route.Login.route) {
                                LoginView(navController)
                            }
                            aniComposable(route = Route.SetPassword.route) {
                                SetPasswordView(
                                    navController,
                                    it.arguments?.getString("words", "") ?: ""
                                )
                            }
                            aniComposable(route = Route.SetEmail.route) {
                                SetEmailView(
                                    navController,
                                    it.arguments?.getString("words", "") ?: ""
                                )
                            }
                            aniComposable(route = Route.PickFaceImage.route) {
                                val backStackEntry = remember {
                                    navController.getBackStackEntry(Route.CompleteProfile.route)
                                }
                                PickFaceImageView(navController, hiltViewModel(backStackEntry))
                            }
                            aniComposable(route = Route.ProfilePreview.route) {
                                val backStackEntry = remember {
                                    navController.getBackStackEntry(Route.CompleteProfile.route)
                                }
                                ProfilePreview(navController, hiltViewModel(backStackEntry))
                            }
                            aniComposable(route = Route.Register1.route) {
                                Register1View(navController)
                            }
                            aniComposable(route = Route.Register2.route) {
                                val backStackEntry = remember {
                                    navController.getBackStackEntry(Route.Register1.route)
                                }
                                Register2View(
                                    navController,
                                    LoginType.Register,
                                    hiltViewModel(backStackEntry)
                                )
                            }
                            aniComposable(route = Route.CompleteProfile.route) {
                                CompleteProfileView(navController)
                            }
                            aniComposable(route = Route.SetName.route) {
                                val backStackEntry = remember {
                                    navController.getBackStackEntry(Route.CompleteProfile.route)
                                }
                                SetNameView(navController, hiltViewModel(backStackEntry))
                            }
                            aniComposable(route = Route.Home.route) {
                                navController.backQueue.removeIf { it.destination.route != Route.Home.route }
                                HomeView(db.userBaseInfoDao())
                            }
                            aniComposable(
                                route = Route.BigImage.route
                            ) {
                                BigImageView(navController = navController, it.requiredArg("url"))
                            }
                            aniComposable(Route.PermissionIntro.route) {
                                PermissionView(navController) { finish() }
                            }
                            aniComposable(Route.NoInternet.route) {
                                NoInternetView(navController)
                            }

                            bottomSheet(Route.ChooseImageSourceBottom.route) {
                                val backStackEntry = remember {
                                    navController.getBackStackEntry(Route.CompleteProfile.route)
                                }
                                ChooseImageSource(navController, hiltViewModel(backStackEntry))
                            }
                            bottomSheet(Route.VideoVoiceChatBottom.route) {
                                VideoVoiceChat(navController)
                            }
                        }
                    }
                }
            }
        }
    }
}