package com.example.springbootmultimodules.controller

import com.example.springbootmultimodules.component.WeatherRequestHandler
import com.example.springbootmultimodules.component.WeatherExceptionHandler
import com.example.springbootmultimodules.service.WeatherService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.IOException
//HTTP 요청을 처리하고, 비즈니스 로직을 호출합니다.
//이 컨트롤러는 API 엔드포인트에 대한 요청을 처리하고,
// 필요한 데이터를 가져와서 비즈니스 로직을 수행합니다.
@RestController
class WeatherAPIController(
    @Autowired private val weatherService: WeatherService,
    @Autowired private val weatherRequestHandler: WeatherRequestHandler,
    @Autowired private val weatherExceptionHandler: WeatherExceptionHandler
) {

    @GetMapping("/api/do", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Transactional
    fun getRegionWeather(@RequestParam regionId: Long): ResponseEntity<String> {
        val region = weatherService.findById(regionId)
        return try {
            val response = region?.let { weatherRequestHandler.getRegionWeather(it) }
            ResponseEntity.ok(response)
        }catch (e:IOException){
            weatherExceptionHandler.handleIOException(e)
        } catch (e: Exception) {
            weatherExceptionHandler.handleException(e)
        }
    }
}
