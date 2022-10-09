package com.ti4n.freechat.model.response.swap

data class SupportTokens(val tokens: Map<String, SupportToken>)

data class SupportToken(
    val symbol: String,
    val name: String,
    val address: String,
    val decimals: String,
    val logoURI: String,
    val tags: List<String>
)
