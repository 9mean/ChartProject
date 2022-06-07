package com.example.chartproject.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chartproject.R
import com.example.chartproject.databinding.ActivityPredictBinding
import kotlin.math.roundToInt

class PredictActivity : AppCompatActivity() {
    lateinit var binding:ActivityPredictBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}