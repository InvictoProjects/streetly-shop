package com.invictoprojects.streetlyshop.web.exception

class UserAlreadyRegisteredException(email: String) : RuntimeException("User with $email has already registered")
