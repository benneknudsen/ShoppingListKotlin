package com.example.shoppingliststartcodekotlin

import android.content.Context
import androidx.preference.PreferenceManager

//Singleton class to handle preferences from any fragment/activity
object PreferenceHandler {

    private const val SETTINGS_NAMEKEY = "name"
    private const val SETTINGS_NOTIFICATONS = "notifications"



    fun getName(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_NAMEKEY, "")!!
    }


    fun useNotifications(context: Context) : Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_NOTIFICATONS,true)
    }


}