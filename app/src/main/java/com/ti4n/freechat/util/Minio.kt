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

    private const val END_POINT = "http://47.57.185.242:10005"
    private const val BUCKET = "openim"

    private val s3 = S3Client {
        region = ""
        credentialsProvider = object : CredentialsProvider {
            override suspend fun getCredentials() = Credentials(
                accessKeyId = "user12345",
                secretAccessKey = "key12345",
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