package com.focus617.webbackendservice.domain.interactors.dtos

data class ProductRegistrationRequest(
    var code: String = "",
    var title: String = "",
    var description: String = "",
    var image: String = "",
    var price: Double = 0.0
)