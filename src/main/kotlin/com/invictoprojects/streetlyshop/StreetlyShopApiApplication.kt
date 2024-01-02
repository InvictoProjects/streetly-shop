package com.invictoprojects.streetlyshop

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class StreetlyShopApiApplication

fun main(args: Array<String>) {
	runApplication<StreetlyShopApiApplication>(*args)
}
