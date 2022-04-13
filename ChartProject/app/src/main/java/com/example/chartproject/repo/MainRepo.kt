package com.example.chartproject.repo

import android.app.Application
import android.util.Log
import com.example.chartproject.datas.Candle
import com.example.chartproject.retrofits.RetrofitChartDataManager

class MainRepo(application: Application) {
    val context=application
    val retrofitChartDataManager=RetrofitChartDataManager.instance
    //서버에 다이어리 추가하기
    suspend fun getChartData(symbol:String,st_date:String,end_date:String):List<Candle>?{
        val request=retrofitChartDataManager.getChartData(symbol= symbol,st_date = st_date,end_date =end_date )
        if (request != null) {
            if(request.isSuccessful)
                Log.d("TAG", "addMyDiary requestbody: ${request.body()}")
            else
                Log.d("TAG", "fail requestbody: ${request.body()}")
            return request.body()?.response
        }
        return null
    }
}