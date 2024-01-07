package com.invictoprojects.streetlyshop.web.controller

import com.invictoprojects.streetlyshop.web.controller.response.ErrorResponse
import com.invictoprojects.streetlyshop.web.exception.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.validation.ConstraintViolationException

@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(Exception::class)
    fun globalException(exception: Exception): ResponseEntity<ErrorResponse> {
        val message = "Error occurred"
        logger.error(message, exception)

        val errors = mutableListOf<String>()
        if (exception.message != null) errors.add(exception.message!!)

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(message, errors))
    }

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun invalidRefreshTokenException(exception: InvalidRefreshTokenException): ResponseEntity<ErrorResponse> {
        val message = "Invalid refresh token was passed"
        logger.error(message, exception)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message, mutableListOf(exception.message!!)))
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun usernameNotFoundException(exception: UsernameNotFoundException): ResponseEntity<ErrorResponse> {
        val message = "User was not found"
        logger.error(message, exception)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message, mutableListOf(exception.message!!)))
    }

    @ExceptionHandler(UserAlreadyRegisteredException::class)
    fun userAlreadyRegisteredException(exception: UserAlreadyRegisteredException): ResponseEntity<ErrorResponse> {
        val message = "User has already registered"
        logger.error(message, exception)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message, mutableListOf(exception.message!!)))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = "Validation failed: ${exception.message}"
        logger.error(message, exception)

        val errors = exception.bindingResult.fieldErrors.map { it.defaultMessage ?: it.field }
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message, errors))
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun usernameNotFoundException(exception: BadCredentialsException): ResponseEntity<ErrorResponse> {
        val message = "Bad credentials"
        logger.error(message, exception)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message, mutableListOf(exception.message!!)))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolationException(exception: ConstraintViolationException): ResponseEntity<ErrorResponse> {
        val message = "Validation failed, ${exception.message}"
        logger.error(message, exception)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message, mutableListOf(exception.message!!)))
    }

    @ExceptionHandler(ProductNotFoundException::class)
    fun productNotFoundException(exception: ProductNotFoundException): ResponseEntity<ErrorResponse> {
        val message = "Product was not found"
        logger.error(message, exception)

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(message, mutableListOf(exception.message!!)))
    }

    @ExceptionHandler(InvalidAttributeException::class)
    fun invalidAttributeException(exception: InvalidAttributeException): ResponseEntity<ErrorResponse> {
        val message = "Attribute is invalid"
        logger.error(message, exception)

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(message, mutableListOf(exception.message!!)))
    }

    @ExceptionHandler(UserNotAuthorizedException::class)
    fun userNotAuthorizedException(exception: UserNotAuthorizedException): ResponseEntity<ErrorResponse> {
        val message = "User is not authorized to perform operation"
        logger.error(message, exception)

        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(message, mutableListOf(exception.message!!)))
    }
}
