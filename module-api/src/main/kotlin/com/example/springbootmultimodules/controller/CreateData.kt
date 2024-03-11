package com.example.springbootmultimodules.controller



import com.example.springbootmultimodules.entity.Region
import com.example.springbootmultimodules.service.WeatherService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import org.springframework.transaction.annotation.Transactional

@RestController
class CreateData @Autowired constructor(private val weatherService: WeatherService) {
    @Value("\${resources.location}")
    private lateinit var resourceLocation: String

    @PostMapping("/region")
    @Transactional
    fun resetRegionList(): ResponseEntity<String> {
        val fileLocation = "$resourceLocation/regionList.csv"
        val path: Path = Paths.get(fileLocation)
        val uri: URI = path.toUri()

        return try {
            BufferedReader(InputStreamReader(UrlResource(uri).inputStream)).use { br ->
                // Skip header
                br.readLine()

                var line: String? = br.readLine()
                while (line != null) {
                    val splits = line.split(",".toRegex()).toTypedArray()
                    val region = Region(
                        splits[0].toLong(),
                        splits[1],
                        splits[2],
                        splits[3].toInt(),
                        splits[4].toInt()
                    )
                    weatherService.save(region)
                    line = br.readLine()
                }
                ResponseEntity.ok("초기화에 성공했습니다")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("뭔가 오류가 생겼는데요")
        }
    }
}