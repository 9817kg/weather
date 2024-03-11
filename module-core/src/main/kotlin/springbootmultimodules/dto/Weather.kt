package com.example.springbootmultimodules.dto

import jakarta.persistence.Embeddable


@Embeddable
open class Weather(
    open var temp: Double, // 온도
    open var rainAmount: Double, // 강수량
    open var humid: Double, // 습도
    open var lastUpdateTime: String // 마지막 갱신 시각 (시간 단위)
)


