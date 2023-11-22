package com.invictoprojects.streetlyshop.service.facade

import com.invictoprojects.streetlyshop.web.exception.AuthenticationRequiredException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class DefaultAuthenticationFacade : AuthenticationFacade {
    override fun getAuthentication(): Authentication {
        return SecurityContextHolder.getContext().authentication ?: throw AuthenticationRequiredException()
    }
}
