package com.focus617.webbackendservice.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class HelloController {

    @GetMapping
    fun helloWorld(): String = "Hello World!"

    @GetMapping("api")
    fun helloApi(): String = "Hello, this is a RESTful endpoint testing site."

}