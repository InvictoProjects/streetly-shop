package com.invictoprojects.streetlyshop.web.config

import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Role
import com.invictoprojects.streetlyshop.web.filter.JWTAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import javax.servlet.http.HttpServletRequest

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthenticationFilter: JWTAuthenticationFilter): SecurityFilterChain {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().cors()
            .and().csrf().disable()
            .authorizeHttpRequests()

            .antMatchers(HttpMethod.POST, "/v1/api/attribute-definition/**")
            .hasAnyRole(Role.SELLER.name, Role.ADMIN.name)
            .antMatchers(HttpMethod.PUT, "/v1/api/attribute-definition/**")
            .hasAnyRole(Role.SELLER.name, Role.ADMIN.name)
            .antMatchers(HttpMethod.GET, "/v1/api/attribute-definition/*/*").permitAll()

            .antMatchers("/v1/api/attribute/search").permitAll()

            .antMatchers(HttpMethod.PUT, "/v1/api/attribute/value/*/name").hasAnyRole(Role.SELLER.name, Role.ADMIN.name)

            .antMatchers("/v1/api/auth/**").permitAll()

            .antMatchers(HttpMethod.POST, "/v1/api/category/**").hasAnyRole(Role.SELLER.name, Role.ADMIN.name)
            .antMatchers(HttpMethod.PUT, "/v1/api/category/**").hasAnyRole(Role.SELLER.name, Role.ADMIN.name)
            .antMatchers(HttpMethod.GET, "/v1/api/category/*/*").permitAll()

            .antMatchers(HttpMethod.POST, "/v1/api/content/**").hasAnyRole(Role.SELLER.name, Role.ADMIN.name)
            .antMatchers(HttpMethod.PUT, "/v1/api/content/**").hasAnyRole(Role.SELLER.name, Role.ADMIN.name)
            .antMatchers(HttpMethod.GET, "/v1/api/content/*/*").permitAll()


            .antMatchers(HttpMethod.POST, "/v1/api/customer/register").permitAll()
            .antMatchers(HttpMethod.POST, "/v1/api/customer/**").authenticated()
            .antMatchers(HttpMethod.PUT, "/v1/api/customer/**").authenticated()
            .antMatchers(HttpMethod.DELETE, "/v1/api/customer/**").authenticated()

            .antMatchers("/v1/api/exchange-rate/**").hasRole(Role.ADMIN.name)

            .antMatchers("/").permitAll()

            .antMatchers("/v1/api/media/**").hasAnyRole(Role.SELLER.name, Role.ADMIN.name)

            .antMatchers("/v1/api/notification/callback").permitAll()

            .antMatchers(HttpMethod.POST, "/v1/api/product").hasAnyRole(Role.SELLER.name, Role.ADMIN.name)
            .antMatchers(HttpMethod.PUT, "/v1/api/product/*").hasAnyRole(Role.SELLER.name, Role.ADMIN.name)
            .antMatchers(HttpMethod.GET, "/v1/api/product/*/*").permitAll()

            .antMatchers("/v1/api/product/search").permitAll()

            .antMatchers("/v1/api/review").authenticated()

            .antMatchers(HttpMethod.POST, "/v1/api/variant").hasAnyRole(Role.SELLER.name, Role.ADMIN.name)
            .antMatchers(HttpMethod.PUT, "/v1/api/variant/*/stock").hasAnyRole(Role.SELLER.name, Role.ADMIN.name)
            .antMatchers(HttpMethod.GET, "/v1/api/variant/*/*").permitAll()

            .antMatchers(
                "/swagger-resources/**",
                "/v3/api-docs",
                "/swagger-ui/**",
                "/webjars/**",
            ).permitAll()

            .anyRequest().authenticated()
            .and().addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter::class.java)
            .httpBasic().disable()
            .formLogin().disable()

        return http.build()
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        return DefaultCorsConfigurationSource()
    }

    companion object {
        const val JWT_HEADER = "Authorization"
    }
}

class DefaultCorsConfigurationSource : CorsConfigurationSource {
    override fun getCorsConfiguration(request: HttpServletRequest): CorsConfiguration {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("*")
        config.allowedMethods = listOf("*")
        config.allowedHeaders = listOf("*")
        config.exposedHeaders = listOf("*")
        config.maxAge = MAX_AGE

        return config
    }

    companion object {
        const val MAX_AGE = 3600L
    }

}
