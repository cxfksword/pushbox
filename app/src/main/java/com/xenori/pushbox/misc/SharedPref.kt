package com.xenori.pushbox.misc

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.reflect.TypeToken

object SharedPref {
    val APP_IGNORE_LIST = "app_ignore_list"
    private lateinit var context : Context

    fun init(ctx : Context) {
        context = ctx
    }

    fun getString(key: String, defaultVal: String, group: Group = Group.APP): String? {
        return context.getSharedPreferences(group.name, Context.MODE_PRIVATE).getString(key, defaultVal);
    }

    fun editor(group: Group = Group.APP) : SharedPreferences.Editor {
        return context.getSharedPreferences(group.name, Context.MODE_PRIVATE).edit()
    }

    fun getIgnoreList() : ArrayList<String> {
        val str = getString(APP_IGNORE_LIST, "")
        if (!str.isNullOrEmpty()) {
            val itemType = object : TypeToken<ArrayList<String>>() {}.type
            val list = JsonUtil.fromJson<ArrayList<String>>(str,itemType)
            if (list != null) {
                return list
            }
        }

        return  ArrayList<String>()
    }

    fun saveIgnoreList(list : List<String>) {
        val str = JsonUtil.toJson(list)
        editor().putString(APP_IGNORE_LIST, str).apply()
    }

    enum class Group(value : String) {
        APP("app")
    }
}