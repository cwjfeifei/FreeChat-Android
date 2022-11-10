package com.ti4n.freechat.wallet

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.ti4n.freechat.db.AppDataBase
import com.ti4n.freechat.db.RecentTransfer
import com.ti4n.freechat.di.dataStore
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.erc20.ERC20Tokens
import com.ti4n.freechat.erc20.ethereum
import com.ti4n.freechat.erc20.wethereum
import com.ti4n.freechat.network.FreeChatApiService
import com.ti4n.freechat.util.EthUtil
import com.ti4n.freechat.util.IM
import com.ti4n.freechat.util.address
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import io.openim.android.sdk.models.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.kethereum.bip39.model.MnemonicWords
import java.math.RoundingMode
import java.util.Date
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class SendMoneyViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    val freeChatApiService: FreeChatApiService,
    val db: AppDataBase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val isRedPack = savedStateHandle.get<Boolean>("redpack") ?: false
    val toUserId = savedStateHandle.get<String>("userId") ?: ""
    val toUserInfo = MutableStateFlow<UserInfo?>(null)

    val tokens = MutableStateFlow(emptyList<ERC20Token>())

    val toAddress = MutableStateFlow(toUserId)
    val amount = MutableStateFlow("")
    val amountUSD = MutableStateFlow(0f)
    val selectedToken = MutableStateFlow<ERC20Token?>(null)
    val fromAddress = IM.currentUserInfo.value?.userID ?: ""
    val remainAmount = MutableStateFlow("")

    val gas = MutableStateFlow("")
    val maxGas = MutableStateFlow("")
    val gasUSD = MutableStateFlow(0f)

    val transactionSend = MutableSharedFlow<Boolean>(0, 1)
    val transactionHash = MutableStateFlow("")

    val recentAddress = Pager(PagingConfig(50), null) {
        db.recentTransferDao().getAllAddress()
    }.flow

    init {
        viewModelScope.launch {
            async {
                tokens.value =
                    listOf(ethereum, wethereum) + freeChatApiService.getSupportTokens().result
                setSelectedToken(tokens.value.first())
            }
            async {
                if (isRedPack && toUserId.isNotEmpty()) {
                    toUserInfo.value = IM.getUserInfo(toUserId)
                    addressDone()
                }
            }
        }
    }

    fun setAddress(address: String) {
        toAddress.value = address
    }

    fun addressDone() {
        if (toAddress.value.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                EthUtil.gasPrice(
                    fromAddress, toAddress.value
                ).collectLatest {
                    it?.let {
                        maxGas.value = it.first
                        gas.value = it.second
                        val rate = freeChatApiService.getRate("ETH-USD").data.first()
                        gasUSD.value = it.second.toFloat() * rate.idxPx.toFloat()
                    }
                }
            }
        }
    }

    fun setAmount(_amount: String) {
        amount.value = _amount
        viewModelScope.launch {
            amountUSD.value =
                (_amount.toFloatOrNull()
                    ?: 0f) * (freeChatApiService.getRate("${selectedToken.value?.symbol}-USD").data.firstOrNull()?.idxPx?.toFloatOrNull()
                    ?: 0f)
        }
    }

    fun setSelectedToken(token: ERC20Token) {
        selectedToken.value = token
        viewModelScope.launch {
            remainAmount.value = if (token.symbol != "ETH") EthUtil.balanceOf(
                token, fromAddress
            )?.toBigDecimal()?.toPlainString() ?: "" else EthUtil.balanceOf(fromAddress)
                ?.toBigDecimal()?.toPlainString() ?: ""
        }

    }

    fun transfer(password: String) {
        viewModelScope.launch {
            if (selectedToken.value?.symbol != "ETH") {
                EthUtil.transferERC20(
                    context,
                    toAddress.value,
                    amount.value,
                    selectedToken.value!!.contractAddress,
                    selectedToken.value!!.Decimals,
                    password,
                ).collectLatest {
                    transactionHash.value = it.transactionHash
                    db.recentTransferDao()
                        .insert(RecentTransfer(toAddress.value, Date(System.currentTimeMillis())))
                    if (isRedPack) {
                        IM.sendRedPackMessage(
                            toUserId,
                            selectedToken.value!!.LogoURI,
                            amount.value,
                            it.transactionHash
                        )
                    }
                    transactionSend.tryEmit(true)
                }
            } else {
                EthUtil.transfer(
                    context,
                    toAddress.value,
                    amount.value,
                    password,
                )?.collectLatest {
                    transactionHash.value = it.transactionHash
                    db.recentTransferDao()
                        .insert(RecentTransfer(toAddress.value, Date(System.currentTimeMillis())))
                    if (isRedPack) {
                        IM.sendRedPackMessage(
                            toUserId,
                            selectedToken.value!!.LogoURI,
                            amount.value,
                            it.transactionHash
                        )
                    }
                    transactionSend.tryEmit(true)
                }
            }
        }
    }
}