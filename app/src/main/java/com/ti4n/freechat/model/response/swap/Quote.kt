package com.ti4n.freechat.model.response.swap

data class Quote(
    val estimatedGas: Int,
    val fromToken: FromToken,
    val fromTokenAmount: String,
    val protocols: List<Protocol>,
    val toToken: ToToken,
    val toTokenAmount: String
) {
    data class FromToken(
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

    data class ToToken(
        val address: String,
        val decimals: Int,
        val logoURI: String,
        val name: String,
        val symbol: String
    )
}