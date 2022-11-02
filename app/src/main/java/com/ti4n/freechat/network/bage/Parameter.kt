package com.ti4n.freechat.network.bage

import okhttp3.RequestBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

import java.io.File

/**
 * Created Parameter
 */
class Parameter {
    private val linkedHashMap: LinkedHashMap<String, RequestBody?>
    private val jsonMap: LinkedHashMap<String, Any>

    init {
        linkedHashMap = LinkedHashMap()
        jsonMap = LinkedHashMap()
    }

    fun buildFrom(): LinkedHashMap<String, RequestBody?> {
        return linkedHashMap
    }

    fun buildMap(): LinkedHashMap<String, Any> {
        return jsonMap
    }

    fun buildJson(): String {
        return GsonHel.toJson(jsonMap)
    }

    fun buildJsonBody(): RequestBody? {
        return RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            buildJson()
        )
    }

    fun add(key: String, oj: Any): Parameter {
        put(key, oj)
        return this
    }

    fun add(key: String, file: File): Parameter {
        put(key, file)
        return this
    }

    fun add(key: String, files: List<File>): Parameter {
        for (file in files) {
            put(key, file)
        }
        return this
    }

    private fun put(key: String, o: Any) {
        var body: RequestBody? = null
        if (o is String) {
            body = RequestBody.create("text/plain".toMediaTypeOrNull(), o)
            jsonMap[key] = o
            linkedHashMap[key] = body
        } else if (o is File) {
            val file = o
            body = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file)
            jsonMap[key] = file.path
            linkedHashMap[key + "\"; filename=\"" + file.name] = body
        } else {
            jsonMap[key] = o
        }
    }
}
