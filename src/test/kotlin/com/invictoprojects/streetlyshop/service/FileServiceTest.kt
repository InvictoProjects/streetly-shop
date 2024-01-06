package com.invictoprojects.streetlyshop.service

import com.azure.core.util.BinaryData
import com.azure.storage.blob.BlobClient
import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.specialized.BlockBlobClient
import com.invictoprojects.streetlyshop.service.model.File
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class FileServiceTest {

    @Mock
    lateinit var blobServiceClient: BlobServiceClient

    @Mock
    lateinit var blobContainerClient: BlobContainerClient

    @Mock
    lateinit var blobClient: BlobClient

    @Mock
    lateinit var blockBlobClient: BlockBlobClient

    @Mock
    lateinit var data: BinaryData

    @InjectMocks
    lateinit var fileService: FileService

    @Test
    fun uploadFile_fileIsValid_fileIsUploaded() {
        given(blobServiceClient.getBlobContainerClient("avatars")).willReturn(blobContainerClient)
        given(blobContainerClient.getBlobClient("filename.jpg")).willReturn(blobClient)
        given(blobClient.blockBlobClient).willReturn(blockBlobClient)
        given(blockBlobClient.blobUrl).willReturn("url")

        val url = fileService.uploadFile(File(data, "filename.jpg"), "avatars")

        assertThat(url).isEqualTo("url")
        verify(blockBlobClient).deleteIfExists()
        verify(blockBlobClient).upload(data, true)
    }

    @Test
    fun uploadFile_clientThrowsException_exceptionIsThrown() {
        given(blobServiceClient.getBlobContainerClient("avatars")).willReturn(blobContainerClient)
        given(blobContainerClient.getBlobClient("filename.jpg")).willReturn(blobClient)
        given(blobClient.blockBlobClient).willReturn(blockBlobClient)
        given(blockBlobClient.upload(data, true)).willThrow(RuntimeException())

        val throwable = catchThrowable { fileService.uploadFile(File(data, "filename.jpg"), "avatars") }

        assertThat(throwable).isInstanceOf(RuntimeException::class.java)
        verify(blockBlobClient).deleteIfExists()
    }

    @Test
    fun deleteFile_fileIsDeleted() {
        given(blobServiceClient.getBlobContainerClient("avatars")).willReturn(blobContainerClient)
        given(blobContainerClient.getBlobClient("filename.jpg")).willReturn(blobClient)
        given(blobClient.blockBlobClient).willReturn(blockBlobClient)

        fileService.deleteFile("filename.jpg", "avatars")

        verify(blockBlobClient).deleteIfExists()
    }
}
