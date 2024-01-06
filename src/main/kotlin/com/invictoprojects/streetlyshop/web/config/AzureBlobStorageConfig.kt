package com.invictoprojects.streetlyshop.web.config

import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.azure.storage.common.StorageSharedKeyCredential
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AzureBlobStorageConfig(
    @Value("\${spring.cloud.azure.storage.blob.account-name}") val accountName: String,
    @Value("\${spring.cloud.azure.storage.blob.account-key}") val accountKey: String,
    @Value("\${spring.cloud.azure.storage.blob.endpoint}") val endpoint: String
) {

    @Bean
    fun blobServiceClient(): BlobServiceClient = BlobServiceClientBuilder()
        .endpoint(endpoint)
        .credential(StorageSharedKeyCredential(accountName, accountKey))
        .buildClient()
}
