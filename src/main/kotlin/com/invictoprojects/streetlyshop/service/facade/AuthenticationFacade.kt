package com.invictoprojects.streetlyshop.service.facade

import org.springframework.security.core.Authentication

interface AuthenticationFacade {
    fun getAuthentication(): Authentication
}
