package com.example.springbootmultimodules.dto

data class WeatherResponseDTO(
    val weather: Weather?,
    val message: String
) {
    data class Builder(
        var weather: Weather? = null,
        var message: String = ""
    ) {
        fun weather(weather: Weather?) = apply { this.weather = weather }
        fun message(message: String) = apply { this.message = message }
        fun build() = WeatherResponseDTO(weather, message)
    }
}
