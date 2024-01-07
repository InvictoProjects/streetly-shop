package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.MediaRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.media.Media
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.service.facade.AuthenticationFacade
import com.invictoprojects.streetlyshop.web.controller.dto.MediaDTO
import com.invictoprojects.streetlyshop.web.controller.dto.toDTO
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

const val MEDIAS_CONTAINER = "medias"

@Service
class MediaService(
    private val mediaRepository: MediaRepository,
    private val fileService: FileService,
    private val authenticationFacade: AuthenticationFacade
) {

    fun upload(multipartFile: MultipartFile): MediaDTO {
        val userId = authenticationFacade.getAuthentication().name.toObjectId()
        val mediaId = ObjectId()

        val file = multipartFile.toFile(userId, mediaId)

        val mediaUrl = fileService.uploadFile(file, MEDIAS_CONTAINER)
        val media = Media(id = mediaId, url = mediaUrl, uploadedBy = userId)

        return mediaRepository.save(media).toDTO()
    }

    fun getUserMedias(userId: String): List<MediaDTO> {
        return mediaRepository.getByUploadedBy(userId.toObjectId()).map { it.toDTO() }
    }
}

