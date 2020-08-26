package com.example.firebasestorage.utils

import android.content.Context

object PreferencesManagement {

    private val PREF_NAME = "user_data"

    fun getUserId(context: Context): String? {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        return pref.getString("userID", null)

    }

    fun saveUserId(context: Context, userID: String?): Boolean {

        val pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val prefsEditor = pref.edit()

        prefsEditor.putString("userID", userID)

        return prefsEditor.commit()

    }

}