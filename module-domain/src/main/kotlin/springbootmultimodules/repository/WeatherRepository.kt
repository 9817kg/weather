package com.example.springbootmultimodules.repository

import com.example.springbootmultimodules.entity.Region
import org.springframework.data.jpa.repository.JpaRepository

interface WeatherRepository :JpaRepository<Region,Long>{
}