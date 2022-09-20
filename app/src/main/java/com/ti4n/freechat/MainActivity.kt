@file:OptIn(ExperimentalMaterialNavigationApi::class)

package com.ti4n.freechat

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.collect
import org.web3j.contracts.eip20.generated.ERC20
import org.web3j.crypto.Credentials
import org.web3j.crypto.Wallet
import org.web3j.crypto.WalletUtils
import org.web3j.ens.contracts.generated.ENS
import org.web3j.ens.contracts.generated.PublicResolver
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameter
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.AbiDefinition
import org.web3j.protocol.http.HttpService
import org.web3j.tx.Contract
import org.web3j.tx.gas.ContractGasProvider
import org.web3j.tx.gas.DefaultGasProvider
import java.math.BigInteger
import javax.inject.Inject
import kotlin.math.pow


@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val mainViewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        IM.init(this)
//        mainViewModel.getAddress()
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
                            startDestination = Route.MainLogin.route
                        ) {
                            composable(route = Route.MainLogin.route) {
                                MainLoginView(navController)
                            }
                            composable(route = Route.Login.route) {
                                LoginView(navController)
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


}

class MainViewModel : ViewModel() {
    fun getAddress() {
        val words =
            EthUtil.getMnemonicCode("crumble forest crop trick rescue light patient talk flock balcony labor ball")
        val web3 =
            Web3j.build(HttpService("https://mainnet.infura.io/v3/a36c7f54cb3244a3b352f922daad690c"))
        viewModelScope.launch(Dispatchers.IO) {
            web3.ethGetBalance(
                words.address().hex,
                DefaultBlockParameterName.LATEST
            ).flowable().collect {
                try {
                    Log.e(
                        "getAddress",
                        "getAddress: ${
                            it.balance.toDouble() / 10.0.pow(18)
                        }"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            ERC20.load(
                "0x171b1daefac13a0a3524fcb6beddc7b31e58e079",
                web3,
                Credentials.create(words.privateKey().key.toString()),
                DefaultGasProvider()
            ).balanceOf(words.address().hex).flowable().collect {
                Log.e(
                    "getAddress",
                    "getAddress FCC: ${
                        it.toDouble() / 10.0.pow(18)
                    }"
                )
            }
        }

        Log.e(
            "getAddress",
            "getAddress: ${
                words.address()
            }"
        )
    }
}