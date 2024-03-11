package com.example.springbootmultimodules

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories("com.example.springbootmultimodules.repository")
@EntityScan("com.example.springbootmultimodules.entity")

class SpringBootMultimodulesApplication

fun main(args: Array<String>) {
	runApplication<SpringBootMultimodulesApplication>(*args)
}
