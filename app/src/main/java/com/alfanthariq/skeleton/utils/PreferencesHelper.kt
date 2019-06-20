package alfanthariq.com.signatureapp.util

import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {
    fun getSettingPref(context: Context) : SharedPreferences {
        val pref: SharedPreferences by lazy {
            context.getSharedPreferences("setting.conf", Context.MODE_PRIVATE)
        }

        return pref
    }

    fun getProfilePref(context: Context) : SharedPreferences {
        val pref: SharedPreferences by lazy {
            context.getSharedPreferences("profile.conf", Context.MODE_PRIVATE)
        }

        return pref
    }
}