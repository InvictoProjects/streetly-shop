package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.service.MediaService
import com.invictoprojects.streetlyshop.web.controller.dto.MediaDTO
import com.invictoprojects.streetlyshop.web.validator.ValidImage
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Api("Media Controller")
@Validated
@RestController
@RequestMapping("/v1/api/media")
class MediaController(
    val mediaService: MediaService
) {
    @ApiOperation("Upload media")
    @PostMapping
    fun uploadMedia(@ValidImage @RequestPart("file") file: MultipartFile): MediaDTO {
        return mediaService.upload(file)
    }

    @ApiOperation("Get user medias")
    @GetMapping("{userId}")
    fun getUserMedias(@PathVariable userId: String): List<MediaDTO> {
        return mediaService.getUserMedias(userId)
    }
}
