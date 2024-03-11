package com.example.springbootmultimodules.entity

import com.example.springbootmultimodules.dto.Weather
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
open class Region(
    @Id @Column(name = "region_id")
    open var id: Long, // 지역 순번

    @Column(name = "region_parent")
    open  var parentRegion: String, // 시, 도

    @Column(name = "region_child") // getter에만 적용되는 애노테이션 사용
    open var childRegion: String, // 시, 군, 구

    open var nx: Int, // x좌표

    open var ny: Int,  // y좌표

    @Embedded open var weather: Weather? // 지역 날씨 정보
) {
    // 날씨 정보 제외하고 지역 생성
    constructor(id: Long, parentRegion: String, childRegion: String, nx: Int, ny: Int) :
            this(id, parentRegion, childRegion, nx, ny, null)




    // 날씨 갱신
    fun updateRegionWeather(weather: Weather) {
        this.weather = weather
    }

    override fun toString(): String {
        return "$parentRegion $childRegion"
    }
}
