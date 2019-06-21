package com.alfanthariq.skeleton.utils

import org.joda.time.format.DateTimeFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateOperationUtil {

    fun getDateFromString(dateStr: String): Date? {
        val d: Date?
        if (dateStr != "0000-00-00 00:00:00") {
            val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
            val dt = formatter.parseDateTime(dateStr)
            d = dt.toDate()
        } else {
            d = null
        }
        return d
    }

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