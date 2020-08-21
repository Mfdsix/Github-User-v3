package com.zgenit.github_user.helper

import android.content.Context
import androidx.core.content.edit

internal class SessionHelper(context: Context){

    companion object {
        private const val PREFS_NAME = "github_user_prefs"
        private const val DAILY_NOTIFICATION = "daily_notification"
    }

    private val session = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var dailyNotification
        set(value) {
            session.edit {
                putBoolean(DAILY_NOTIFICATION, value)
            }
        }
        get() = session.getBoolean(DAILY_NOTIFICATION, false)

}