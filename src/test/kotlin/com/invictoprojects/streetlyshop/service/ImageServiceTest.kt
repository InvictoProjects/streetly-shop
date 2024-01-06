package com.invictoprojects.streetlyshop.service

import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockMultipartFile
import org.springframework.util.ResourceUtils

@ExtendWith(MockitoExtension::class)
internal class ImageServiceTest {

    @InjectMocks
    lateinit var imageService: ImageService

    @Test
    fun toAvatar_fileIsValid_avatarIsCreated() {
        val file = ResourceUtils.getFile("classpath:test/avatar/cat.jpeg")
        val multipartFile = MockMultipartFile(file.name, file.name, "image/jpeg", file.readBytes())

        val userId = ObjectId()
        val resultFile = imageService.toAvatar(multipartFile, userId)

        assertThat(resultFile.name).isEqualTo("$userId.jpeg")

        val expectedFile = ResourceUtils.getFile("classpath:test/avatar/cat-avatar.jpeg")
        assertThat(expectedFile.readBytes()).isEqualTo(resultFile.data.toBytes())
    }
}
