package com.ti4n.freechat.network.bage

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * json解析帮助类
 */
class GsonHel private constructor() {
    init {
        mGson = GsonBuilder().serializeNulls().create()
    }

    private class ParameterizedTypeImpl : ParameterizedType {

        private val raw: Class<*>
        private val  args: Array<Type>

        constructor(raw: Class<*>, args: Array<Type>) {
            this.raw = raw
            this.args = args
        }

        override fun getActualTypeArguments(): Array<Type> {
            return args
        }

        override fun getRawType(): Type {
            return rawType
        }

        override fun getOwnerType(): Type? {
            return null
        }
    }

    companion object {
        private var instance: GsonHel? = null
        private var mGson: Gson? = null
        @Synchronized
        private fun get(): GsonHel? {
            if (instance == null) {
                instance = GsonHel()
            }
            return instance
        }

        fun toJson(`object`: Any?): String {
            get()
            return mGson!!.toJson(`object`)
        }

        fun <T> fromJson(json: String?, aClass: Class<T>?): T {
            get()
            return mGson!!.fromJson(json, aClass)
        }

//        /**
//         * 处理 data 为 object 的情况
//         *
//         * @param jsonStr
//         * @param clazz
//         * @param <T>
//         * @return
//        </T> */
//        @Throws(Exception::class)
//        fun <T> dataObject(jsonStr: String?, clazz: Class<T>): Base<T> {
//            get()
//            val type: Type = ParameterizedTypeImpl(Base::class.java, Array<T>(clazz))
//            return mGson!!.fromJson(jsonStr, type)
//        }
//
//        /**
//         * 处理 data 为 array 的情况
//         *
//         * @param jsonStr
//         * @param clazz
//         * @param <T>
//         * @return
//        </T> */
//        @Throws(Exception::class)
//        fun <T> dataArray(jsonStr: String?, clazz: Class<T>): Base<List<T>> {
//            get()
//            // 生成List<T> 中的 Type
//            val listType: Type =
//                ParameterizedTypeImpl(MutableList::class.java, arrayOf<Class<*>>(clazz))
//            // 根据List<T>生成的，再生出完整的Base<List<T>>
//            val type: Type = ParameterizedTypeImpl(Base::class.java, arrayOf(listType))
//            return mGson!!.fromJson(jsonStr, type)
//        }
    }
}

