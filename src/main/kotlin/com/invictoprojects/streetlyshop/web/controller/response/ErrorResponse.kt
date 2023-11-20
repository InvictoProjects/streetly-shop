package com.invictoprojects.streetlyshop.web.controller.response

data class ErrorResponse(
    val message: String,
    val errors: List<String> = mutableListOf()
)
