package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.media.Media
import java.time.Instant

data class MediaDTO(
    val id: String,
    var url: String,
    var uploadedBy: String,
    val uploadDate: Instant
)

fun Media.toDTO(): MediaDTO {
    return MediaDTO(
        id = id.toString(),
        url = url,
        uploadedBy = uploadedBy.toString(),
        uploadDate = uploadDate
    )
}
