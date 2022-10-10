package com.ti4n.freechat.model.response.swap

data class Quote(
    val estimatedGas: Int,
    val fromToken: Token,
    val fromTokenAmount: String,
    val protocols: List<Protocol>,
    val toToken: Token,
    val toTokenAmount: String
) {
    data class Token(
        val address: String,
        val decimals: Int,
        val logoURI: String,
        val name: String,
        val symbol: String
    )

    data class Protocol(
        val fromTokenAddress: String,
        val name: String,
        val part: Int,
        val toTokenAddress: String
    )
}