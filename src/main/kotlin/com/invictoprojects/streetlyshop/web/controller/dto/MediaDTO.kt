package com.invictoprojects.streetlyshop.web.controller.dto

import java.time.Instant

data class MediaDTO(
    val id: String,
    var url: String,
    var uploadedBy: String,
    val uploadDate: Instant
)
