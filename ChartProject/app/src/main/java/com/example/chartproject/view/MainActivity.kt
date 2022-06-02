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
import com.example.chartproject.utils.*
import com.example.chartproject.vm.MainVm
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    lateinit var curDate:String
    lateinit var preDate:String
    var count=0
    var loadFlag:Boolean=false
    var saveChartList=ArrayList<CandleEntry>()
    private val mainVm: MainVm by lazy {
        ViewModelProvider(this,MainVm.Factory(application)).get(MainVm::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        curDate= getCurrentDate()
        preDate= getBefor3MDate(curDate)
        initChart()
        loadChart()

        mainVm.cartDataList.observe(this@MainActivity,{
            Log.d("TAG", "loadChart: cartDataList $it")
            lifecycleScope.launch {
                //delay(1500)
                loadFlag=true
                setChartData(it)
                loadAfterSetView()
            }

        })

    }
    private fun loadAfterSetView(){
    Log.d("TAG", "loadAfterSetView: ")
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
            mainVm.getChartData("US2YT", preDate, curDate)
            //데이터를 다 받았으면 옵저빙해서 뷰에 뿌려주기
            withContext(Main){

            }
        }
    }
    fun initChart() {
        binding.apply {
            mainChartView.description.isEnabled = false
            mainChartView.setVisibleXRangeMaximum(20f)
            mainChartView.isScrollContainer=true
            mainChartView.setTouchEnabled(true)
            mainChartView.setPinchZoom(true)
            mainChartView.setDrawGridBackground(false)
            mainChartView.isDragXEnabled=true
            // x축 설정
            mainChartView.xAxis.apply {
                textColor = Color.TRANSPARENT
                position = XAxis.XAxisPosition.BOTTOM
                // 세로선 표시 여부 설정
                //axisMaximum=15f

                this.setDrawGridLines(false)
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
                textColor = Color.WHITE
                // 가로선 표시 여부 설정
                setDrawGridLines(true)
                // 차트의 오른쪽 테두리 라인 설정
                setDrawAxisLine(true)
                axisLineColor = Color.rgb(50, 59, 76)
                gridColor = Color.rgb(50, 59, 76)
            }

            mainChartView.legend.isEnabled = true
            setChartLegend(0)
            mainChartView.setOnTouchListener(object :OnSwipeTouchListener(this@MainActivity){
                override fun onSwipeLeft() {
                    Toast.makeText(this@MainActivity,"왼쪽으로",Toast.LENGTH_SHORT).show()
                }
                override fun onSwipeRight() {
                    if(loadFlag){
                        Toast.makeText(this@MainActivity,"데이터를 불러오는 중..",Toast.LENGTH_SHORT).show()
                        lifecycleScope.launch {
                            mainSpinKit.visibility=View.VISIBLE
                            delay(1500)
                            loadFlag=false
                            mainVm.getChartData("US2YT", preDate, curDate)

                        }
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
    fun setChartLegend(mainIndicatorType: Int) {
        val legendList: List<LegendEntry> = when (mainIndicatorType) {
            0  -> {
                binding.mainChartView.legend.isEnabled = true
                val movingAverageLegend = LegendEntry()
                movingAverageLegend.label = "단순 MA"
                // form을 사용하지 않는 legend의 경우에는
                // form 값을 NONE으로 설정하면 된다.
                movingAverageLegend.form = Legend.LegendForm.NONE
                val averageN1Legend = LegendEntry()
                averageN1Legend.label = "5"
                averageN1Legend.formColor = Color.rgb(219, 17, 179)
                val averageN2Legend = LegendEntry()
                averageN2Legend.label = "10"
                averageN2Legend.formColor = Color.rgb(11, 41, 175)
                val averageN3Legend = LegendEntry()
                averageN3Legend.label = "20"
                averageN3Legend.formColor = Color.rgb(234, 153, 1)
                val averageN4Legend = LegendEntry()
                averageN4Legend.label = "60"
                averageN4Legend.formColor = Color.rgb(253, 52, 0)
                val averageN5Legend = LegendEntry()
                averageN5Legend.label = "120"
                averageN5Legend.formColor = Color.rgb(170, 170, 170)
                listOf(
                    movingAverageLegend,
                    averageN1Legend,
                    averageN2Legend,
                    averageN3Legend,
                    averageN4Legend,
                    averageN5Legend
                )
            }
            else -> {
                binding.mainChartView.legend.isEnabled = true
                val movingAverageLegend = LegendEntry()
                movingAverageLegend.label = "단순 MA"
                // form을 사용하지 않는 legend의 경우에는
                // form 값을 NONE으로 설정하면 된다.
                movingAverageLegend.form = Legend.LegendForm.NONE
                val averageN1Legend = LegendEntry()
                averageN1Legend.label = "5"
                averageN1Legend.formColor = Color.rgb(219, 17, 179)
                val averageN2Legend = LegendEntry()
                averageN2Legend.label = "10"
                averageN2Legend.formColor = Color.rgb(11, 41, 175)
                val averageN3Legend = LegendEntry()
                averageN3Legend.label = "20"
                averageN3Legend.formColor = Color.rgb(234, 153, 1)
                val averageN4Legend = LegendEntry()
                averageN4Legend.label = "60"
                averageN4Legend.formColor = Color.rgb(253, 52, 0)
                val averageN5Legend = LegendEntry()
                averageN5Legend.label = "120"
                averageN5Legend.formColor = Color.rgb(170, 170, 170)
                listOf(
                    movingAverageLegend,
                    averageN1Legend,
                    averageN2Legend,
                    averageN3Legend,
                    averageN4Legend,
                    averageN5Legend
                )
            }
        }
        binding.mainChartView.legend.apply {
            // legend 데이터 설정
            setCustom(legendList)
            // legend 텍스트 컬러 설정
            textColor = Color.WHITE
            // legend의 위치를 좌측 상단으로 설정함
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            // 수평 방향으로 정렬함
            orientation = Legend.LegendOrientation.HORIZONTAL
            setDrawInside(true)
        }
    }
    fun setChartData2(candles: ArrayList<Candle>) {
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
        //새로 추가한 데이터 개수만큼 기존의 데이터리스트에 x축요소에 더해주어 겹치지않게함
        if(!saveChartList.isEmpty()){
            for(candle in saveChartList){
                candle.x+=i
                Log.d("TAG", "candle.x : ${candle.x}")
            }
        }
        curDate= getBefor1Day(candles[0].date)
        preDate= getBefor3MDate(curDate)
        //Log.d("TAG", "setChartData after curDate: $curDate ")
        priceEntries.addAll(saveChartList)
        saveChartList=priceEntries
        Log.d("TAG", "saveChartList size ${ saveChartList.size}")
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
            //this.data = CandleData(priceDataSet)
            Log.d("TAG", "priceDataSet: $priceDataSet")
            /*this.data.notifyDataChanged()
            notifyDataSetChanged()
            */
            invalidate()
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
        //새로 추가한 데이터 개수만큼 기존의 데이터리스트에 x축요소에 더해주어 겹치지않게함
        if(saveChartList.isNotEmpty()){
            for(candle in saveChartList){
                candle.x+=i
                Log.d("TAG", "candle.x : ${candle.x}")
            }
        }
        curDate= getBefor1Day(candles[0].date)
        preDate= getBefor3MDate(curDate)
        //Log.d("TAG", "setChartData after curDate: $curDate ")
        priceEntries.addAll(saveChartList)
        saveChartList=priceEntries
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
            // CombinedData 만들어주고
            val combinedData = CombinedData()
            // CombinedData에 CandleData를 추가해주고
            combinedData.setData(CandleData(priceDataSet))
            // LineData 만들어주고
            val lineData = getMovingAverage(saveChartList)
            // CombinedData에 LineData를 추가해주고
            combinedData.setData(lineData)
            // CombinedChart에 CombinedData를 적용시켜주면 된다.
            this.data = combinedData
            invalidate()
        }
    }

    // 이동평균선 데이터를 갖는 LineData를 리턴하는 함수
    fun getMovingAverage(candles: ArrayList<CandleEntry>): LineData {
        // 마지막에 리턴할 LineData 만들어주고
        val ret: LineData = LineData()

        val N1: Int = 5
        val N2: Int = 10
        val N3: Int = 20
        val N4: Int = 60
        val N5: Int = 120
        // LineChart에 추가할 데이터 == ArrayList<Entry> 생성해주고
        val averageN1Entries = ArrayList<Entry>()
        val averageN2Entries = ArrayList<Entry>()
        val averageN3Entries = ArrayList<Entry>()
        val averageN4Entries = ArrayList<Entry>()
        val averageN5Entries = ArrayList<Entry>()
        var count: Int = 0
        var sumN1: Float = 0.0f
        var sumN2: Float = 0.0f
        var sumN3: Float = 0.0f
        var sumN4: Float = 0.0f
        var sumN5: Float = 0.0f
        // 이동평균선 데이터를 계산하는 for loop
        // 데이터 생성은 Entry(createdAt, price) 로 하면 된다.
        for (candle in candles) {
            count++
            sumN1 += candle.close
            sumN2 += candle.close
            sumN3 += candle.close
            sumN4 += candle.close
            sumN5 += candle.close
            val now = candles.indexOf(candle)
            if (count >= N5) {
                averageN5Entries.add(
                    Entry(
                        count.toFloat(),
                        sumN5 / N5.toFloat()
                    )
                )
                sumN5 -= candles[now - (N5 - 1)].close
            }
            if (count >= N4) {
                averageN4Entries.add(
                    Entry(
                        count.toFloat(),
                        sumN4 / N4.toFloat()
                    )
                )
                sumN4 -= candles[now - (N4 - 1)].close
            }
            if (count >= N3) {
                averageN3Entries.add(
                    Entry(
                        count.toFloat(),
                        sumN3 / N3.toFloat()
                    )
                )
                sumN3 -= candles[now - (N3 - 1)].close
            }
            if (count >= N2) {
                averageN2Entries.add(
                    Entry(
                        count.toFloat(),
                        sumN2 / N2.toFloat()
                    )
                )
                sumN2 -= candles[now - (N2 - 1)].close
            }


            if (count >= N1) {
                averageN1Entries.add(Entry(count.toFloat(), sumN1 / N1.toFloat()))
                sumN1 -= candles[now - (N1 - 1)].close
            }
        }

        // for loop에서 만든 Entry들로 LineDataSet을 만들어준다.
        val averageN1DataSet = LineDataSet(averageN1Entries, "").apply {
            setDrawCircles(false)
            color = Color.rgb(219, 17, 179)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val averageN2DataSet = LineDataSet(averageN2Entries, "").apply {
            setDrawCircles(false)
            color = Color.rgb(11, 41, 175)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val averageN3DataSet = LineDataSet(averageN3Entries, "").apply {
            setDrawCircles(false)
            color = Color.rgb(234, 153, 1)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val averageN4DataSet = LineDataSet(averageN4Entries, "").apply {
            setDrawCircles(false)
            color = Color.rgb(253, 52, 0)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        val averageN5DataSet = LineDataSet(averageN5Entries, "").apply {
            setDrawCircles(false)
            color = Color.rgb(170, 170, 170)
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0f
            lineWidth = 1.0f
        }
        // LineData에 LineDataSet을 추가해준다.
        ret.addDataSet(averageN1DataSet)
        ret.addDataSet(averageN2DataSet)
        ret.addDataSet(averageN3DataSet)
        ret.addDataSet(averageN4DataSet)
        ret.addDataSet(averageN5DataSet)

        return ret
    }
}