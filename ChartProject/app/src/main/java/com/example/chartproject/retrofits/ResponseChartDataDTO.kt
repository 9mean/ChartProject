package com.example.chartproject.retrofits

import com.example.chartproject.datas.Candle

data class ResponseChartDataDTO(
    var success:String?,
    var response:List<Candle>?,
    var apiError:String?
)
