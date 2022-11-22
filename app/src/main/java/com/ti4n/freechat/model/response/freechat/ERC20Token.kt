package com.ti4n.freechat.model.response.freechat

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ERC20Token(
    val Abi: String,
    val Address: String,
    val ChainID: Int,
    val Decimals: Int,
    val LogoURI: String,
    val Name: String,
    val bitcointalk: String,
    val blog: String,
    val blueCheckmark: String,
    val contractAddress: String,
    val description: String,
    val discord: String,
    val divisor: String,
    val email: String,
    val facebook: String,
    val github: String,
    val linkedin: String,
    val reddit: String,
    val slack: String,
    val symbol: String,
    val telegram: String,
    val tokenName: String,
    val tokenPriceUSD: String,
    val tokenType: String,
    val totalSupply: String,
    val twitter: String,
    val website: String,
    val wechat: String,
    val whitepaper: String,
) : Parcelable

@Parcelize
data class ERC20Tokens(val result: List<ERC20Token>) : Parcelable