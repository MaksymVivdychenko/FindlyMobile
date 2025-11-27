package com.example.findly.utils

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_LOGIN = "user_login"

    private lateinit var prefs: SharedPreferences

    // Цей метод треба викликати 1 раз при старті (в MainActivity)
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthData(token: String, userId: String, userLogin : String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_LOGIN, userLogin)
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun getLogin(): String? {
        return prefs.getString(KEY_LOGIN, "Користувач")
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}