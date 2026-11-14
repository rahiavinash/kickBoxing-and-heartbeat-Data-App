package com.app.kickboxing.Activitys.utils

import org.json.JSONObject

object Utils {

    fun <T> toJsonObject(vararg pairs: Pair<String, T>): JSONObject {
        val parentObject = JSONObject()
        pairs.forEach { pair ->
            parentObject.put(pair.first, pair.second)
        }
        return parentObject
    }

}