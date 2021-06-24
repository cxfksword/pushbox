package com.xenori.pushbox.misc

import com.google.gson.Gson
import java.lang.Exception
import java.lang.reflect.Type

object JsonUtil {
    private val gson = Gson()

    fun toJson(src : Any): String? {
        return gson.toJson(src)
    }

    fun <T> fromJson(src: String, typeOfT: Type): T? {
        try {
            return gson.fromJson<T>(src, typeOfT)
        } catch (ex : Exception) {
            ex.printStackTrace()
        }

        return null
    }
}