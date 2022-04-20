package com.example.chartproject.view

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.chartproject.databinding.ActivityMainBinding
import com.example.chartproject.datas.Candle
import com.example.chartproject.retrofits.RetrofitChartDataService
import com.example.chartproject.retrofits.RetrofitClient
import com.example.chartproject.retrofits.ServerAccess
import com.example.chartproject.utils.OnSwipeTouchListener
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
    lateinit var curDate:String
    lateinit var preDate:String
    var saveChartList=ArrayList<CandleEntry>()
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
        binding.mainSpinKit.visibility=View.GONE
    }
    private fun loadChart(){
        
        lifecycleScope.launch(Dispatchers.IO) {
            //뷰모델에서 서버로부터 차트요청하고
            curDate= getCurrentDate()
            preDate= getBefor3MDate()
            mainVm.getChartData("US2YT", preDate, curDate)
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
            mainChartView.description.isEnabled = false
            mainChartView.setMaxVisibleValueCount(200)
            mainChartView.setPinchZoom(false)
            mainChartView.setDrawGridBackground(false)
            // x축 설정
            mainChartView.xAxis.apply {
                textColor = Color.TRANSPARENT
                position = XAxis.XAxisPosition.BOTTOM
                // 세로선 표시 여부 설정
                this.setDrawGridLines(true)
                axisLineColor = Color.rgb(50, 59, 76)
                gridColor = Color.rgb(50, 59, 76)
            }
            // 왼쪽 y축 설정
            mainChartView.axisLeft.apply {
                textColor = Color.WHITE
                isEnabled = false
            }
            // 오른쪽 y축 설정
            mainChartView.axisRight.apply {
                setLabelCount(7, false)
                textColor = Color.WHITE
                // 가로선 표시 여부 설정
                setDrawGridLines(true)
                // 차트의 오른쪽 테두리 라인 설정
                setDrawAxisLine(true)
                axisLineColor = Color.rgb(50, 59, 76)
                gridColor = Color.rgb(50, 59, 76)
            }
            mainChartView.legend.isEnabled = false
            mainChartView.setOnTouchListener(object :OnSwipeTouchListener(this@MainActivity){
                override fun onSwipeLeft() {
                    //Toast.makeText(this@MainActivity,"왼쪽으로",Toast.LENGTH_SHORT).show()
                }
                override fun onSwipeRight() {
                    Toast.makeText(this@MainActivity,"왼쪽으로",Toast.LENGTH_SHORT).show()
                    lifecycleScope.launch {
                        mainVm.getChartData("US2YT", preDate, curDate)
                        mainSpinKit.visibility=View.VISIBLE
                    }
                }
                override fun onSwipeTop() {
                    //Toast.makeText(this@MainActivity,"위로",Toast.LENGTH_SHORT).show()
                }
                override fun onSwipeBottom() {
                    //Toast.makeText(this@MainActivity,"아래로",Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    fun setChartData(candles: ArrayList<Candle>) {

        var priceEntries = ArrayList<CandleEntry>()
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

        priceEntries= (priceEntries+saveChartList) as ArrayList<CandleEntry>
        saveChartList=priceEntries
        Log.d("TAG", "setChartData: ${priceEntries.size}")
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

        binding.mainChartView.apply {
            this.data = CandleData(priceDataSet)
            invalidate()
        }
    }
}