package com.example.springbootmultimodules.component

import com.example.springbootmultimodules.dto.WeatherResponseDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.io.IOException
// 예외 처리 클래스
@Component
class WeatherExceptionHandler {

    private val log = LoggerFactory.getLogger(WeatherExceptionHandler::class.java)

    fun handleIOException(e: IOException): ResponseEntity<String> {
        log.error("IOException : {}", e.message)
        val dto = WeatherResponseDTO.Builder()
            .weather(null)
            .message("IOException").build()
        return ResponseEntity.ok(ObjectMapper().writeValueAsString(dto))
    }

    fun handleException(e: Exception): ResponseEntity<String> {
        log.error("알수 없는 에러: {}", e.message)
        val dto = WeatherResponseDTO.Builder()
            .weather(null)
            .message("알수 없는 에러: ${e.message}").build()
        return ResponseEntity.ok(ObjectMapper().writeValueAsString(dto))
    }
}