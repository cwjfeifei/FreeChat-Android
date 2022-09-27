package com.ti4n.freechat.model.response

data class Transaction(
    val blockHash: String,
    val blockNumber: String,
    val confirmations: String,
    val contractAddress: String,
    val cumulativeGasUsed: String,
    val from: String,
    val functionName: String,
    val gas: String,
    val gasPrice: String,
    val gasUsed: String,
    val hash: String,
    val input: String,
    val isError: String,
    val methodId: String,
    val nonce: String,
    val timeStamp: String,
    val to: String,
    val transactionIndex: String,
    val txreceipt_status: String,
    val value: String
)