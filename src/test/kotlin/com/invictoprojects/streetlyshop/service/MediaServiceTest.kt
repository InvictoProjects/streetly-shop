package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.MediaRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.media.Media
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.BDDMockito.given
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class MediaServiceTest {

    @Mock
    lateinit var mediaRepository: MediaRepository

    @Mock
    lateinit var authenticationFacade: AuthenticationFacade

    @Mock
    lateinit var fileService: FileService

    @InjectMocks
    lateinit var mediaService: MediaService

    @Test
    fun upload_fileIsValid_mediaIsUploaded() {
        val userId = ObjectId()

        val authentication = UsernamePasswordAuthenticationToken(userId.toString(), null)
        given(authenticationFacade.getAuthentication()).willReturn(authentication)

        given(fileService.uploadFile(any(), any())).willReturn("media.url")
        given(mediaRepository.save(any())).willAnswer(AdditionalAnswers.returnsFirstArg<Media>())

        val file = MockMultipartFile("avatar.png", "Image".toByteArray())

        val mediaDTO = mediaService.upload(file)

        assertThat(mediaDTO.uploadedBy).isEqualTo(userId.toString())
        assertThat(mediaDTO.url).isEqualTo("media.url")
    }

    @Test
    fun getUserMedias_mediasAreFound_mediaDTOsAreReturned() {
        val userId = ObjectId()
        val medias = listOf(Media(id = ObjectId(), url = "media.url", uploadedBy = userId))

        given(mediaRepository.getByUploadedBy(userId)).willReturn(medias)

        val mediaDTOs = mediaService.getUserMedias(userId.toString())

        assertThat(mediaDTOs).hasSize(1)
        assertThat(mediaDTOs[0].url).isEqualTo("media.url")
    }

    private fun <T> any(): T = Mockito.any()
}
