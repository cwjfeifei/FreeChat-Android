package com.ti4n.freechat.wallet

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ti4n.freechat.paging.EthTransactionPagingSourceFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TokenDetailSimplyViewModel @Inject constructor(
    pagingSourceFactory: EthTransactionPagingSourceFactory,
    savedStateHandle: SavedStateHandle
) :
    ViewModel() {

    val address = savedStateHandle.get<String>("address") ?: ""
    val tokenAddress = savedStateHandle.get<String>("tokenAddress") ?: ""

    val pager = Pager(PagingConfig(20)) {
        pagingSourceFactory.create(address, tokenAddress)
    }.flow.cachedIn(viewModelScope)
}