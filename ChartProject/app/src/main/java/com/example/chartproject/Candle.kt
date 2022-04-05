package com.example.chartproject

data class Candle(
    val createdAt: Float,
    val open: Float,
    val close: Float,
    val shadowHigh: Float,
    val shadowLow: Float
)