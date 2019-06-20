package com.alfanthariq.skeleton.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateOperationUtil {

    fun getCurrentTimeStr(formatStr : String) : String {
        val format = SimpleDateFormat(formatStr, Locale.US)
        val cal = Calendar.getInstance()
        val tgl = format.format(cal.time)

        return tgl
    }

    fun getTimeStr(formatStr : String, date : Date) : String {
        val format = SimpleDateFormat(formatStr, Locale.US)
        val tgl = format.format(date)

        return tgl
    }

    fun dateStrFormat(formatInput : String, formatOutput : String, date : String) : String {
        val dateFormat = SimpleDateFormat(formatInput, Locale.US)
        val convertedDate: Date
        try {
            convertedDate = dateFormat.parse(date)
            val newformat = SimpleDateFormat(formatOutput, Locale.US)
            val finalDateString = newformat.format(convertedDate)
            return finalDateString
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return ""
    }
}