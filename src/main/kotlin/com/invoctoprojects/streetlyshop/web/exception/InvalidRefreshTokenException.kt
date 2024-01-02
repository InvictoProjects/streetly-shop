package com.invoctoprojects.streetlyshop.web.exception

import org.bson.types.ObjectId

class InvalidRefreshTokenException(userId: ObjectId) :
    RuntimeException("Invalid refresh token was passed for user $userId")
