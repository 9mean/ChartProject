package com.example.chartproject.retrofits

import retrofit2.Response

class RetrofitChartDataManager {
    companion object{
        val instance=RetrofitChartDataManager()
    }
    //레트로핏 인터페이스 가져오기
    private val retrofitService: RetrofitChartDataService? =
        RetrofitClient.getClient(ServerAccess.baseUrl)?.create(
            RetrofitChartDataService::class.java
        )
    //차트데이터보내기
    suspend fun getChartData(
        symbol: String, st_date:String,end_date:String
    ): Response<ResponseChartDataDTO>? {
        val params = HashMap<String, Any>()
        params["symbol"] = symbol
        params["st_date"] = st_date
        params["end_date"] = end_date
        return retrofitService?.getChartData(params)
    }

}