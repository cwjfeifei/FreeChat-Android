package com.ti4n.freechat.util

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import aws.sdk.kotlin.services.s3.putObject
import aws.smithy.kotlin.runtime.content.asByteStream
import java.io.File

fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

fun Context.getPathFromUri(uri: Uri): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = contentResolver.query(uri, proj, null, null, null)
    try {
        cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)?.let {
            cursor.moveToFirst()
            return cursor.getString(it)
        }
        return null
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    } finally {
        cursor?.close()
    }
}
