@file:OptIn(ExperimentalMaterialNavigationApi::class)

package com.ti4n.freechat

import android.os.Bundle
import android.util.Log
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
import cash.z.ecc.android.bip39.Mnemonics
import cash.z.ecc.android.bip39.toSeed
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
import com.ti4n.freechat.bip44forandroidlibrary.utils.Bip44Utils
import com.ti4n.freechat.bip44forandroidlibrary.utils.Utils
import com.ti4n.freechat.bottomsheet.ChooseImageSource
import com.ti4n.freechat.bottomsheet.VideoVoiceChat
import com.ti4n.freechat.home.HomeView
import com.ti4n.freechat.login.*
import com.ti4n.freechat.network.TronApiService
import com.ti4n.freechat.profile.ProfileView
import com.ti4n.freechat.ui.theme.FreeChatTheme
import com.ti4n.freechat.util.*
import com.ti4n.freechat.widget.BigImageView
import dagger.hilt.android.AndroidEntryPoint
import org.tron.trident.core.key.KeyPair
import javax.inject.Inject


@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        IM.init(this)
        getAddress()
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
                            startDestination = Route.Home.route
                        ) {
                            composable(route = Route.MainLogin.route) {
                                MainLoginView(navController)
                            }
                            composable(route = Route.Login.route) {
                                Register2View(navController, LoginType.Login)
                            }
                            composable(route = Route.SetPassword.route) {
                                SetPasswordView(navController)
                            }
                            composable(route = Route.Register1.route) {
                                Register1View(navController)
                            }
                            composable(route = Route.Register2.route) {
                                val backStackEntry = remember {
                                    navController.getBackStackEntry(Route.Register1.route)
                                }
                                Register2View(
                                    navController,
                                    LoginType.Register,
                                    hiltViewModel(backStackEntry)
                                )
                            }
                            composable(route = Route.CompleteProfile.route) {
                                CompleteProfileView(navController)
                            }
                            composable(route = Route.SetName.route) {
                                val backStackEntry = remember {
                                    navController.getBackStackEntry(Route.CompleteProfile.route)
                                }
                                SetNameView(navController, hiltViewModel(backStackEntry))
                            }
                            composable(route = Route.Home.route) {
                                navController.backQueue.removeIf { it.destination.route != Route.Home.route }
                                HomeView(navController)
                            }
                            composable(
                                route = Route.BigImage.route
                            ) {
                                BigImageView(navController = navController, it.requiredArg("url"))
                            }
                            composable(
                                route = Route.Profile.route
                            ) {
                                ProfileView(navController = navController)
                            }

                            bottomSheet(Route.ChooseImageSourceBottom.route) {
                                ChooseImageSource(navController)
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

    @Inject
    lateinit var tronApiService: TronApiService

    fun getAddress() {
        val words =
            TronUtil.getMnemonicCode("where olympic crop zebra boy fruit apart patrol admit world they grab")
        Log.e(
            "getAddress",
            "getAddress: ${
                words.joinToString(" ")
            }"
        )
        val key = words.toKeyPair()
        Log.e(
            "getAddress",
            "private: ${
                key.toPrivateKey()
            }\npublic: ${key.toPublicKey()}"
        )
        Log.e(
            "getAddress",
            "address: ${
                key.toBase58CheckAddress()
            }"
        )
        Log.e(
            "getAddress",
            "address: ${
                key.trc20Contract().transfer("TEPKqUKcJiB4iUaXGBzBsojZKcyomi44Mh", 100, 8, "测试", 100000000)
            }"
        )
    }
}