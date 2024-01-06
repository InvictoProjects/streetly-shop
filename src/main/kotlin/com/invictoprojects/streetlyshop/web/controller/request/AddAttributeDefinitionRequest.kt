package com.invictoprojects.streetlyshop.web.controller.request

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class AddAttributeDefinitionRequest(
    @field:NotBlank
    val name: String?,
    val starred: Boolean = false,
    val searchable: Boolean = false,
    val priority: Long = 1,
    @field:Size(min = 1)
    val values: List<String> = mutableListOf()
)
