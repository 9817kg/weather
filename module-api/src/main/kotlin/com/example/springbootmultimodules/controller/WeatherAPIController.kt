package com.example.springbootmultimodules.controller

import com.example.springbootmultimodules.dto.Weather
import com.example.springbootmultimodules.dto.WeatherResponseDTO
import com.example.springbootmultimodules.entity.Region
import com.example.springbootmultimodules.service.WeatherService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.slf4j.LoggerFactory
import org.springframework.web.client.getForObject

@RestController
@RequestMapping("/api/do")
class WeatherAPIController(
    private val restTemplate: RestTemplate,
    @Autowired private val weatherService: WeatherService // WeatherService 주입
) {

    private val log = LoggerFactory.getLogger(WeatherAPIController::class.java)

    @Value("\${weatherApi.serviceKey}")
    private lateinit var serviceKey: String

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Transactional
    fun getRegionWeather(@RequestParam regionId: Long, model: Model): ResponseEntity<String> {
        val region = weatherService.findById(regionId) // WeatherService로부터 Region 엔티티 조회

        val now = LocalDateTime.now()
        val yyyyMMdd = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        var hour = now.hour
        val min = now.minute
        if (min <= 30) {
            hour -= 1
        }
        val hourStr = "${hour}00"

        val nx = region?.nx.toString()
        val ny = region?.ny.toString()
        val currentChangeTime = now.format(DateTimeFormatter.ofPattern("yy.MM.dd ")) + hour

        val prevWeather = region?.weather
        if (prevWeather != null) {
            if (prevWeather.lastUpdateTime == currentChangeTime) {
                log.info("Reusing existing data")
                val dto = WeatherResponseDTO.Builder()
                    .weather(prevWeather)
                    .message("OK").build()
                return ResponseEntity.ok(ObjectMapper().writeValueAsString(dto))
            } else {
                // 만약 db에 데이터가 없다면  HTTP status 204 Return
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
            }
        }

        log.info("Sending API request >>> Region: $region, Date: $yyyyMMdd, Time: $hourStr")
        model.addAttribute("name", region?.childRegion)
        try {
            val url =
                "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst" +
                        "?serviceKey=${serviceKey}&pageNo=1&numOfRows=1000&dataType=JSON&base_date=${yyyyMMdd}&base_time=${hourStr}&nx=${nx}&ny=${ny}"
            val uri: URI = UriComponentsBuilder.fromHttpUrl(url).build().toUri()
            log.info("Request URL: $url")

            val response = restTemplate.getForObject<String>(uri)

            val jObject = ObjectMapper().readTree(response.trim())

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

            region?.updateRegionWeather(weather)
            val dto = WeatherResponseDTO.Builder()
                .weather(weather)
                .message("OK").build()

            return ResponseEntity.ok(ObjectMapper().writeValueAsString(dto))

        } catch (e: IOException) {
            val dto = WeatherResponseDTO.Builder()
                .weather(null)
                .message("IOException").build()
            return ResponseEntity.ok(ObjectMapper().writeValueAsString(dto))
        } catch (e: Exception) {
            log.error("Invalid JSON response: {}", e.message)
            val dto = WeatherResponseDTO.Builder()
                .weather(null)
                .message("An unknown error occurred: ${e.message}").build()

            return ResponseEntity.ok(ObjectMapper().writeValueAsString(dto))
        }
    }
}
