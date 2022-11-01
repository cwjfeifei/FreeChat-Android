package com.ti4n.freechat.util

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import aws.sdk.kotlin.runtime.endpoint.AwsEndpoint
import aws.sdk.kotlin.runtime.endpoint.AwsEndpointResolver
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.putObject
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import aws.smithy.kotlin.runtime.content.asByteStream
import java.io.File

object Minio {

    private const val END_POINT = "https://freechatoss.s3.ap-southeast-1.amazonaws.com"
    private const val BUCKET = "freechatoss"

    private val s3 = S3Client {
        region = "ap-southeast-1"
        credentialsProvider = object : CredentialsProvider {
            override suspend fun getCredentials() = Credentials(
                accessKeyId = "AKIAR57G45STDV42P4MX",
                secretAccessKey = "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",
                sessionToken = null,
                providerName = null,
            )

        }
        endpointResolver = AwsEndpointResolver { _, _ ->
            AwsEndpoint(END_POINT)
        }
    }

    suspend fun uploadFile(path: String): String? {
        val file = File(path)
        return try {
            s3.putObject {
                bucket = BUCKET
                key = file.name
                body = file.asByteStream()
            }
            "$END_POINT/$BUCKET/${file.name}"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun uploadFile(context: Context, uri: Uri): String? {
        context.getPathFromUri(uri)?.let {
            return uploadFile(it)
        }
        return null
    }
}