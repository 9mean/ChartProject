package com.example.chartproject.utils

import android.net.ParseException
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

//문자열이 제이슨 형태인지, 제이슨 배열 형태인지
fun String?.isJsonObject(): Boolean=this?.startsWith("{")==true && this.endsWith("}")
fun String?.isJsonArray():Boolean=this?.startsWith("[")==true && this.endsWith("]")
fun getMilliFromDate(dateFormat: String?): Long {
    var date = Date()
    val formatter = SimpleDateFormat("yyyy/MM/dd")
    try {
        date = formatter.parse(dateFormat)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    Log.d("TAG", "Today is $date datetime ${date.time}")
    return date.time
}