package com.alfanthariq.skeleton.utils

import android.annotation.SuppressLint
import android.content.Context
import android.telephony.TelephonyManager
import android.os.Build



object HardwareUtil {
    private var mTelephonyManager: TelephonyManager? = null

    @SuppressLint("MissingPermission")
    fun getDeviceImei(context : Context) : String? {
        mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager?
        val imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mTelephonyManager?.imei
        } else {
            mTelephonyManager?.getDeviceId()
        }
        return imei
    }

    @SuppressLint("MissingPermission")
    fun getSerialNumber() : String {
        val sn = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Build.getSerial()
        } else {
            Build.SERIAL
        }
        return sn
    }
}