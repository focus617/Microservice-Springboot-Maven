package com.focus617.serviceregistryeureka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class ServiceRegistryEurekaApplication

fun main(args: Array<String>) {
	runApplication<ServiceRegistryEurekaApplication>(*args)
}
