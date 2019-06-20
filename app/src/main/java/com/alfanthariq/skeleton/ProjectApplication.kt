package com.alfanthariq.skeleton

import android.app.Application
import android.content.Context
import com.alfanthariq.skeleton.data.local.AppDatabase
import com.alfanthariq.skeleton.features.socketioservice.AppSocketListener
import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class ProjectApplication : Application() {

    lateinit var context : Context

    companion object {
        var database: AppDatabase? = null

        fun getDB(context: Context) : AppDatabase?{
            ProjectApplication.database = AppDatabase.getInstance(context)
            return AppDatabase.getInstance(context)
        }
    }

    fun initializeSocket() {
        AppSocketListener.getInstance().setContext(context)
        AppSocketListener.getInstance().initialize()
    }

    fun destroySocketListener() {
        AppSocketListener.getInstance().destroy()
    }

    override fun onTerminate() {
        super.onTerminate()
        destroySocketListener()
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        getDB(this)

        initializeSocket()

        CalligraphyConfig.initDefault(CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        )
    }
}