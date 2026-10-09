package com.example.fitforge

import android.content.Context

object UserPrefs {
    private const val PREF_NAME = "UserPrefs"
    private const val KEY_USERID = "User_iD"
    private const val KEY_NAME = "Full_name"
    private const val KEY_WEIGHT = "Weight"
    private const val KEY_HEIGHT = "Height"
    private const val KEY_LOGIN = "IS_LOGGED_IN"

    fun saveUser(
        context: Context,
        userID: Int,
        name: String,
        weight: Int,
        height: Int
    ) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean(KEY_LOGIN, true)
            putInt(KEY_USERID, userID)
            putString(KEY_NAME, name)
            putInt(KEY_WEIGHT, weight)
            putInt(KEY_HEIGHT, height)
            apply()
        }
    }
    fun getName(context: Context): String =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_NAME, "Guest").toString()

    fun getUserID(context: Context): Int =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_USERID, -1)

    fun getWeight(context: Context): Int =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_WEIGHT, 70)
}
//same for cycling