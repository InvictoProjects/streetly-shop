package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.service.ReviewService
import com.invictoprojects.streetlyshop.web.controller.dto.ReviewDTO
import com.invictoprojects.streetlyshop.web.controller.request.AddReviewRequest
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Api("Review Controller")
@Validated
@RestController
@RequestMapping("/v1/api/review")
class ReviewController(
    val reviewService: ReviewService
) {

    @ApiOperation("Add review")
    @PostMapping
    fun addReview(@Valid @RequestBody request: AddReviewRequest): ReviewDTO =
        reviewService.addReview(request)
}
