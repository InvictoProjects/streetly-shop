package com.invictoprojects.streetlyshop.web.validator

import org.springframework.web.multipart.MultipartFile
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@MustBeDocumented
@Constraint(validatedBy = [ImageValidator::class])
annotation class ValidImage(
    val message: String = "File is not valid image, only png, jpg, jpeg are allowed",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class ImageValidator : ConstraintValidator<ValidImage, MultipartFile> {
    override fun isValid(file: MultipartFile?, context: ConstraintValidatorContext?): Boolean {
        if (file == null || file.contentType == null || file.originalFilename.isNullOrBlank()) return false
        return isContentTypeSupported(file.contentType!!)
    }

    private fun isContentTypeSupported(contentType: String): Boolean {
        val supportedTypes = listOf("image/png", "image/jpg", "image/jpeg")
        return supportedTypes.contains(contentType)
    }
}
