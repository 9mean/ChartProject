package com.example.chartproject.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.chartproject.datas.Candle
import com.example.chartproject.repo.MainRepo
import com.example.chartproject.utils.getMilliFromDate
import kotlinx.coroutines.launch

class MainVm(application: Application): AndroidViewModel(application){
    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return MainVm(application) as T
        }
    }
    private val repo= MainRepo(application)
 //   var chartDataList=ArrayList<Candle>()
    var chartDataSaveList=ArrayList<Candle>()
    private var _chartDataList= MutableLiveData<ArrayList<Candle>>()
    val cartDataList: LiveData<ArrayList<Candle>>
        get() = _chartDataList

    init {

    }

    //서버에서 채권 갖고오기
    suspend fun getChartData(symbol:String,st_date:String,end_date:String){
        viewModelScope.launch {
            val response=repo.getChartData(symbol,st_date,end_date)
            chartDataSaveList= response as ArrayList<Candle>
            //chartDataSaveList.sortWith(compareBy { getMilliFromDate(it.date) })
            //Log.d("TAG", "chartDataSaveList: ${chartDataSaveList}")
            _chartDataList.postValue(chartDataSaveList)

        }
    }
}