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
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    try {
        date = formatter.parse(dateFormat)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    //Log.d("TAG", "Today is $date datetime ${date.time}")
    return date.time
}
fun getCurrentDate(): String {
    val cal = Calendar.getInstance()
    Log.d("TAG", "getCurrentDate: ${"${cal.get(Calendar.YEAR)}" + "-${cal.get(Calendar.MONTH)+1}" + "-${cal.get(Calendar.DATE)}"}")
    return "${cal.get(Calendar.YEAR)}" + "-${cal.get(Calendar.MONTH)+1}" + "-${cal.get(Calendar.DATE)}"
}
fun getBefor3MDate():String{
    val cal = Calendar.getInstance()
    val format = "yyyy-MM-dd"
    val sdf = SimpleDateFormat(format)
    Log.d("TAG", "getBefor3MDate 1 : ${cal.get(Calendar.MONTH)}")
    cal.add(cal.get(Calendar.MONTH),-12) //세달 전 ( Calendar.MONTH -1당 7일로 계산됨)
    val date = sdf.format(cal.time)
    Log.d("TAG", "getBefor3MDate 2 : $date")
    return date
}