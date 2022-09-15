package com.ti4n.freechat.model.response

data class GeneratedAddress(val privateKey: String, val address: String, val hexAddress: String)
data class CreatedAddress(val base58checkAddress: String, val value: String)
