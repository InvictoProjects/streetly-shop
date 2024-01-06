package com.invictoprojects.streetlyshop.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.invictoprojects.streetlyshop.service.MediaService
import com.invictoprojects.streetlyshop.web.controller.dto.MediaDTO
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class MediaControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var objectMapper: ObjectMapper

    @Mock
    lateinit var mediaService: MediaService

    @InjectMocks
    lateinit var controller: MediaController

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
        objectMapper = jacksonObjectMapper()
        objectMapper.registerModule(JavaTimeModule())
    }

    @Test
    fun uploadMedia_fileIsValid_mediaDTOIsReturned() {
        val file = MockMultipartFile("file", "me.jpg", "image/jpg", "image".toByteArray())

        val mediaDTO = MediaDTO(
            id = ObjectId().toString(),
            url = "https://monomarketstorage/medias/6460ba36c919bd5b2298dba8-64731b9c27b41b28e42dd7b7.png",
            uploadedBy = ObjectId().toString(),
            uploadDate = Instant.now()
        )

        given(mediaService.upload(file)).willReturn(mediaDTO)

        val response = mockMvc.perform(
            MockMvcRequestBuilders.multipart("/v1/api/media")
                .file(file)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andReturn().response

        assertThat(response.status).isEqualTo(HttpStatus.OK.value())
        assertThat(response.contentAsString).isEqualTo(objectMapper.writeValueAsString(mediaDTO))
    }

    @Test
    fun getUserMedias_userIdIsValid_mediasAreReturned() {
        val uploadedBy = ObjectId().toString()

        val medias = listOf(
            MediaDTO(
                id = ObjectId().toString(),
                url = "https://monomarketstorage/medias/6460ba36c919bd5b2298dba8-64731b9c27b41b28e42dd7b7.png",
                uploadedBy = uploadedBy,
                uploadDate = Instant.now()
            ),
            MediaDTO(
                id = ObjectId().toString(),
                url = "https://monomarketstorage/medias/6460ba36c919bd5b2298dba8-64731b9c27b41b28e42dd7b8.png",
                uploadedBy = uploadedBy,
                uploadDate = Instant.now()
            )
        )

        given(mediaService.getUserMedias(uploadedBy)).willReturn(medias)

        val actualResponse = mockMvc.perform(
            get("/v1/api/media/$uploadedBy")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andReturn().response

        AssertionsForInterfaceTypes.assertThat(actualResponse.contentAsString)
            .isEqualTo(objectMapper.writeValueAsString(medias))
    }
}
