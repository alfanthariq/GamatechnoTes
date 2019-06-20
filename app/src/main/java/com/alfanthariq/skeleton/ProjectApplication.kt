package com.alfanthariq.skeleton

import android.app.Application
import android.content.Context
import com.alfanthariq.skeleton.data.local.AppDatabase
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class ProjectApplication : Application() {

    companion object {
        var database: AppDatabase? = null

        fun getDB(context: Context) : AppDatabase?{
            ProjectApplication.database = AppDatabase.getInstance(context)
            return AppDatabase.getInstance(context)
        }
    }

    override fun onCreate() {
        super.onCreate()
        getDB(this)

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}