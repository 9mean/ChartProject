package com.example.chartproject.view

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.chartproject.databinding.ActivityMainBinding
import com.example.chartproject.datas.Candle
import com.example.chartproject.retrofits.RetrofitChartDataService
import com.example.chartproject.retrofits.RetrofitClient
import com.example.chartproject.retrofits.ServerAccess
import com.example.chartproject.utils.getBefor3MDate
import com.example.chartproject.utils.getCurrentDate
import com.example.chartproject.utils.getMilliFromDate
import com.example.chartproject.vm.MainVm
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private val mainVm: MainVm by lazy {
        ViewModelProvider(this,MainVm.Factory(application)).get(MainVm::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initChart()
        loadChart()

    }
    private fun loadAfterSetView(){
        binding.mainTopDivider.visibility= View.VISIBLE
        binding.mainPredictBtn.visibility=View.VISIBLE
        binding.mainSettingBtn.visibility=View.VISIBLE
        binding.mainPredictBtn.setOnClickListener {
            val intent = Intent(this, PredictActivity::class.java)
            startActivity(intent)
        }
    }
    private fun loadChart(){
        
        lifecycleScope.launch(Dispatchers.IO) {
            //뷰모델에서 서버로부터 차트요청하고

            mainVm.getChartData("US2YT", getBefor3MDate(), getCurrentDate())
            //데이터를 다 받았으면 옵저빙해서 뷰에 뿌려주기
            withContext(Main){
                mainVm.cartDataList.observe(this@MainActivity,{
                    setChartData(it)
                    loadAfterSetView()
                })

            }
        }
    }
    fun initChart() {
        binding.apply {
            priceChart.description.isEnabled = false
            priceChart.setMaxVisibleValueCount(200)
            priceChart.setPinchZoom(false)
            priceChart.setDrawGridBackground(false)
            // x축 설정
            priceChart.xAxis.apply {
                textColor = Color.TRANSPARENT
                position = XAxis.XAxisPosition.BOTTOM
                // 세로선 표시 여부 설정
                this.setDrawGridLines(true)
                axisLineColor = Color.rgb(50, 59, 76)
                gridColor = Color.rgb(50, 59, 76)
            }
            // 왼쪽 y축 설정
            priceChart.axisLeft.apply {
                textColor = Color.WHITE
                isEnabled = false
            }
            // 오른쪽 y축 설정
            priceChart.axisRight.apply {
                setLabelCount(7, false)
                textColor = Color.WHITE
                // 가로선 표시 여부 설정
                setDrawGridLines(true)
                // 차트의 오른쪽 테두리 라인 설정
                setDrawAxisLine(true)
                axisLineColor = Color.rgb(50, 59, 76)
                gridColor = Color.rgb(50, 59, 76)
            }
            priceChart.legend.isEnabled = false
        }
    }
    fun setChartData(candles: ArrayList<Candle>) {
        val priceEntries = ArrayList<CandleEntry>()
        var i=0
        for (candle in candles) {
            // 캔들 차트 entry 생성
            priceEntries.add(
                CandleEntry(
                    i.toFloat(),
                    candle.high,
                    candle.low,
                    candle.open,
                    candle.close
                )
            )
            i+=1
            //Log.d("TAG", "setChartData: $i 번쨰 캔들")
        }

        val priceDataSet = CandleDataSet(priceEntries, "").apply {
            axisDependency = YAxis.AxisDependency.LEFT
            // 심지 부분 설정
            shadowColor = Color.LTGRAY
            shadowWidth = 0.7F
            // 음봉 설정
            decreasingColor = Color.rgb(18, 98, 197)
            decreasingPaintStyle = Paint.Style.FILL
            // 양봉 설정
            increasingColor = Color.rgb(200, 74, 49)
            increasingPaintStyle = Paint.Style.FILL

            neutralColor = Color.rgb(6, 18, 34)
            setDrawValues(false)
            // 터치시 노란 선 제거
            highLightColor = Color.TRANSPARENT
        }

        binding.priceChart.apply {
            this.data = CandleData(priceDataSet)
            invalidate()
        }
    }
}