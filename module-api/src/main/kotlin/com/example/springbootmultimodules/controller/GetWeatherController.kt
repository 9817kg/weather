package com.example.springbootmultimodules.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class GetWeatherController {

    @GetMapping("/weather")
    fun getWeather():String{
        return "weather"

    }


}
