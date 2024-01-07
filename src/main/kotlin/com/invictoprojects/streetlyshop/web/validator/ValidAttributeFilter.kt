package com.invictoprojects.streetlyshop.web.validator

import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass


@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@MustBeDocumented
@Constraint(validatedBy = [AttributeFilterValidator::class])
annotation class ValidAttributeFilter(
    val message: String = "Filter is not valid",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class AttributeFilterValidator : ConstraintValidator<ValidAttributeFilter, List<List<String>>> {
    override fun isValid(filter: List<List<String>>?, context: ConstraintValidatorContext?): Boolean {
        if (filter == null) return true
        return filter.isNotEmpty() && filter.none { it.isEmpty() }
    }
}
