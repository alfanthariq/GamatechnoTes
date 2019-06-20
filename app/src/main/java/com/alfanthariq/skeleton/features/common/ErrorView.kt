package com.alfanthariq.skeleton.features.common

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.alfanthariq.skeleton.R
import com.livinglifetechway.k4kotlin.onClick
import kotlinx.android.synthetic.main.error_view.view.*

class ErrorView : RelativeLayout {
    private var errorListener: ErrorListener? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        gravity = Gravity.CENTER
        LayoutInflater.from(context).inflate(R.layout.error_view, this)
    }

    fun setView(isError: Boolean, message: String?, case:Int? = 0, listener:ErrorListener) {
        lyt_disconnect.visibility = View.VISIBLE
        if (isError) {
            lyt_offline.visibility = View.VISIBLE
            if (case == 2) img_error.setImageResource(R.drawable.ic_signal_wifi_off)
            else if (case == 1) img_error.setImageResource(R.drawable.ic_gps_off)
            else img_error.setImageResource(R.drawable.ic_bug_report)
            if (message != null) txt_connection.text = message
            lyt_offline.onClick {
                listener.onReloadData()
            }
        } else {
            lyt_disconnect.visibility = View.GONE
            lyt_offline.visibility = View.GONE
        }
    }

    fun setErrorListener(errorListener: ErrorListener) {
        this.errorListener = errorListener
    }

    interface ErrorListener {
        fun onReloadData()
    }
}