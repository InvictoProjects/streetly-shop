package com.invictoprojects.streetlyshop.persistence.impl

import org.bson.types.ObjectId

fun String.toObjectId() = ObjectId(this)
