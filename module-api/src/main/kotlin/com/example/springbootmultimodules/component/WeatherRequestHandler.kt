package com.example.springbootmultimodules.component

import com.example.springbootmultimodules.dto.Weather
import com.example.springbootmultimodules.dto.WeatherResponseDTO
import com.example.springbootmultimodules.entity.Region
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
//비즈니스 로직을 포함하고 있습니다.
// API를 통해 날씨 정보를 가져오는 작업과 관련된 로직 수행하는 클래스
@Component
class WeatherRequestHandler(

    private val restTemplate: RestTemplate
) {
    @Value("\${weatherApi.serviceKey}")
    private lateinit var serviceKey: String


    @Transactional
    fun getRegionWeather(region: Region): String {
        
        val weather = fetchWeatherFromAPI(region)
        return ObjectMapper().writeValueAsString(WeatherResponseDTO.Builder()
            .weather(weather)
            .message("OK")
            .build())
    }

    private fun fetchWeatherFromAPI(region: Region): Weather {

        val now = LocalDateTime.now()
        val yyyyMMdd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        var hour = now.hour
        val min = now.minute
        if (min <= 30) {
            hour -= 1
        }
        val hourStr = "${hour}00"
        val nx = region.nx.toString()
        val ny = region.ny.toString()
        val currentChangeTime = now.format(DateTimeFormatter.ofPattern("yy.MM.dd ")) + hour

        val url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst" +
                "?serviceKey=${serviceKey}&pageNo=1&numOfRows=1000&dataType=JSON&base_date=${yyyyMMdd}&base_time=${hourStr}&nx=${nx}&ny=${ny}"

        val response = restTemplate.getForObject(url, String::class.java)

        val jObject = ObjectMapper().readTree(response?.trim())
        val responseBody = jObject.get("response")
        val body = responseBody.get("body")
        val items = body.get("items")
        val jArray = items.get("item")

        var temp: Double = 0.0
        var humid: Double = 0.0
        var rainAmount: Double = 0.0

        for (i in 0 until jArray.size()) {
            val obj = jArray[i]
            val category = obj.get("category").asText()
            val obsrValue = obj.get("obsrValue").asDouble()

            when (category) {
                "T1H" -> temp = obsrValue
                "RN1" -> rainAmount = obsrValue
                "REH" -> humid = obsrValue
            }
        }
        val weather = Weather(temp, rainAmount, humid, currentChangeTime)
        region.updateRegionWeather(weather)

        return weather
    }
}
