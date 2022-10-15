package com.ti4n.freechat.model.response.swap

data class SwapResponse(
    val fromToken: Quote.Token,
    val fromTokenAmount: String,
//    val protocols: List<String>,
    val toToken: Quote.Token,
    val toTokenAmount: String,
    val tx: Transaction
) {
    data class Transaction(
        val from: String,
        val to: String,
        val data: String,
        val value: String,
        val gasPrice: String,
        val gas: String
    )
}
