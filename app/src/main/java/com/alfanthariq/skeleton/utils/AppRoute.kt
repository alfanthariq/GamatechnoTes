package com.alfanthariq.skeleton.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.alfanthariq.skeleton.features.main.MainActivity

object AppRoute {
    fun open(context : Context, page : String){
        var intent = Intent()
        when (page) {
            "main" -> intent = Intent(context, MainActivity::class.java)
        }

        context.startActivity(intent)
    }

    fun openWithParam(context : Context, page : String, param : HashMap<String, String>){
        var intent = Intent()
        when (page) {
            "main" -> intent = Intent(context, MainActivity::class.java)
        }

        param.forEach {
            intent.putExtra(it.key, it.value)
        }
        context.startActivity(intent)
    }

    fun openResult(activity : Activity, page : String, requestCode : Int){
        var intent = Intent()
        when (page) {
            "main" -> intent = Intent(activity.baseContext, MainActivity::class.java)
        }

        activity.startActivityForResult(intent, requestCode)
    }
}