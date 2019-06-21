package com.alfanthariq.skeleton.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.alfanthariq.skeleton.features.main.MainActivity

object AppRoute {
    fun open(context : Context, cls : Class<*>){
        val intent = Intent(context, cls)
        context.startActivity(intent)
    }

    fun open(context : Context, cls : Class<*>, param : HashMap<String, String>){
        val intent = Intent(context, cls)
        param.keys.forEach {
            intent.putExtra(it, param[it])
        }

        context.startActivity(intent)
    }

    fun open(context : Context, activity : Activity, cls : Class<*>, requestCode : Int){
        val intent = Intent(context, cls)
        activity.startActivityForResult(intent, requestCode)
    }

    fun open(context : Context, activity : Activity, cls : Class<*>, param : HashMap<String, String>, requestCode : Int){
        val intent = Intent(context, cls)
        param.keys.forEach {
            intent.putExtra(it, param[it])
        }

        activity.startActivityForResult(intent, requestCode)
    }
}