package com.invictoprojects.streetlyshop.web.controller.dto

import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Gender
import java.time.Instant

data class CustomerDTO(
    val id: String,
    val name: String? = null,
    val surname: String? = null,
    val middleName: String? = null,
    val avatar: String? = null,
    val phone: String? = null,
    val birthDay: Long? = null,
    val gender: Gender? = null,
    val nickname: String,
    val email: String,
    val registeredAt: Instant
)

fun Customer.toDTO(): CustomerDTO {
    return CustomerDTO(
        id = id.toString(),
        name = name,
        surname = surname,
        middleName = middleName,
        avatar = avatar,
        phone = phone,
        birthDay = birthDay,
        gender = gender, nickname = nickname,
        email = email,
        registeredAt = registeredAt
    )
}
