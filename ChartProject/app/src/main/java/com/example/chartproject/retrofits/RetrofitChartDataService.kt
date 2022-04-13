package com.example.chartproject.retrofits

import retrofit2.Response
import retrofit2.http.*

interface RetrofitChartDataService {
    @GET("api/bond/")
    suspend fun getChartData( @QueryMap parameters: HashMap<String, Any>): Response<ResponseChartDataDTO>
}