package com.invictoprojects.streetlyshop.persistence.impl

import com.invictoprojects.streetlyshop.persistence.domain.model.Language

fun getCollection(collection: String, language: Language) = "$collection${language.name}"
