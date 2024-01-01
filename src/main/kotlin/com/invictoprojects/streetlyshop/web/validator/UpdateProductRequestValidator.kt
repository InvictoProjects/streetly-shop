package com.invictoprojects.streetlyshop.web.validator

import com.invictoprojects.streetlyshop.web.controller.request.UpdateProductRequest
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@MustBeDocumented
@Constraint(validatedBy = [UpdateProductRequestValidator::class])
annotation class ValidUpdateProductRequest(
    val message: String = "Request is not valid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class UpdateProductRequestValidator : ConstraintValidator<ValidUpdateProductRequest, UpdateProductRequest> {
    override fun isValid(request: UpdateProductRequest?, context: ConstraintValidatorContext?): Boolean {
        if (request == null) {
            addErrorMessage(context, "Request is null.")
            return false
        }

        if (request.categoryId == null && request.attributes == null && request.productStatus == null) {
            addErrorMessage(
                context,
                "At least one property should be present (categoryId, attributes, or productStatus)."
            )
            return false
        }

        if (request.attributes != null && request.attributes.isEmpty()) {
            addErrorMessage(context, "Attribute list should not be empty.")
            return false
        }

        return true
    }

    private fun addErrorMessage(context: ConstraintValidatorContext?, message: String) {
        context?.disableDefaultConstraintViolation()
        context?.buildConstraintViolationWithTemplate(message)?.addConstraintViolation()
    }
}
