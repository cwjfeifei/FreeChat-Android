package com.ti4n.freechat.wallet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ti4n.freechat.erc20.ERC20Token
import com.ti4n.freechat.erc20.ERC20Tokens
import com.ti4n.freechat.model.response.Transaction
import com.ti4n.freechat.paging.EthTransactionPagingSource
import com.ti4n.freechat.paging.EthTransactionPagingSourceFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TokenTransactionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    erc20Tokens: ERC20Tokens
) :
    ViewModel() {

    @Inject
    lateinit var pagingSourceFactory: EthTransactionPagingSourceFactory
    val erc20Token =
        erc20Tokens.result.find { it.symbol == savedStateHandle.get<String>("tokenSymbol") }
    val address = savedStateHandle.get<String>("address") ?: ""
    val pager = Pager(PagingConfig(20)) {
        pagingSourceFactory.create(address, erc20Token?.contractAddress)
    }.flow.cachedIn(viewModelScope)

}