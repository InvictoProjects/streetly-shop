package com.invictoprojects.streetlyshop.web.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.*
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SwaggerConfig {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.OAS_30)
            .apiInfo(apiInfo())
            .securityContexts(listOf(securityContext()))
            .securitySchemes(listOf(apiKey()))
            .groupName("streetly-shop-api")
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.invictoprojects.streetlyshop"))
            .paths(PathSelectors.any())
            .build()
    }

    private fun apiKey() = ApiKey(SecurityConfig.JWT_HEADER, "JWT", "header")

    private fun securityContext() = SecurityContext.builder().securityReferences(defaultAuth()).build()

    private fun defaultAuth(): List<SecurityReference> {
        val authorizationScope = AuthorizationScope("global", "accessEverything")
        return listOf(
            SecurityReference(
                SecurityConfig.JWT_HEADER,
                listOf(authorizationScope).toTypedArray()
            )
        )
    }

    private fun apiInfo(): ApiInfo {
        return ApiInfo(
            "Streetly Shop API",
            "REST API",
            "1.0",
            "Terms of service",
            Contact("Manager", "", "streetly@gmail.com"),
            "License",
            "license url",
            listOf()
        )
    }
}
