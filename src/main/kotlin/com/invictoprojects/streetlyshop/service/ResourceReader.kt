package com.invictoprojects.streetlyshop.service

import org.apache.commons.io.IOUtils
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import java.nio.charset.StandardCharsets

object ResourceReader {
    fun readResource(path: String): String {
        val resourceLoader = DefaultResourceLoader()
        val resource = resourceLoader.getResource(path)
        return resource.asString()
    }
}

fun Resource.asString(): String {
    return IOUtils.toString(inputStream, StandardCharsets.UTF_8.name())
}
