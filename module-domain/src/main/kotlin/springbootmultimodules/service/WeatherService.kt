package com.example.springbootmultimodules.service

import com.example.springbootmultimodules.entity.Region
import com.example.springbootmultimodules.repository.WeatherRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WeatherService(private val weatherRepository: WeatherRepository) {
    fun findById(id: Long): Region? {
        return weatherRepository.findById(id).orElse(null)
    }
    fun save(region: Region): Region {
        return weatherRepository.save(region)
    }
}
