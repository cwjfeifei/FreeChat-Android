package com.ti4n.freechat.model.response.freechat

data class Rate(
    val high24h: String,
    val idxPx: String,
    val instId: String,
    val low24h: String,
    val open24h: String,
    val sodUtc0: String,
    val sodUtc8: String,
    val ts: String
)