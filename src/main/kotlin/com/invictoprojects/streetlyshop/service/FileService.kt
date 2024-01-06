package com.invictoprojects.streetlyshop.service

import com.azure.storage.blob.BlobServiceClient
import com.invictoprojects.streetlyshop.service.model.File
import org.springframework.stereotype.Service

@Service
class FileService(val blobServiceClient: BlobServiceClient) {

    fun uploadFile(file: File, containerName: String): String {
        val client = getBlockBlobClient(containerName, file.name)
        client.deleteIfExists()
        client.upload(file.data, true)
        return client.blobUrl
    }

    fun deleteFile(fileName: String, containerName: String) {
        val client = getBlockBlobClient(containerName, fileName)
        client.deleteIfExists()
    }

    private fun getBlockBlobClient(containerName: String, fileName: String) = blobServiceClient
        .getBlobContainerClient(containerName)
        .getBlobClient(fileName)
        .blockBlobClient
}
