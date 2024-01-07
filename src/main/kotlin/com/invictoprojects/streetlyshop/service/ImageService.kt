package com.invictoprojects.streetlyshop.service

import com.azure.core.util.BinaryData
import com.invictoprojects.streetlyshop.service.model.File
import org.apache.commons.io.FilenameUtils
import org.bson.types.ObjectId
import org.imgscalr.Scalr
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.lang.Integer.min
import javax.imageio.ImageIO

@Service
class ImageService {

    fun toAvatar(file: MultipartFile, userId: ObjectId): File {
        val bufferedImage = Scalr.resize(cropImage(file.inputStream), TARGET_SIZE)
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(bufferedImage, file.getExtension(), outputStream)

        val fileName = file.generateFileName(userId)
        val data = BinaryData.fromBytes(outputStream.toByteArray())
        return File(data, fileName)
    }

    private fun cropImage(imageInputStream: InputStream): BufferedImage {
        val originalImage = ImageIO.read(imageInputStream)

        if (originalImage.width == originalImage.height) return originalImage
        val squareSize = min(originalImage.width, originalImage.height)

        return originalImage.getSubimage(
            getUpperLeftCornerX(originalImage.width, squareSize),
            getUpperLeftCornerY(originalImage.height, squareSize),
            squareSize, squareSize
        )
    }

    private fun getUpperLeftCornerX(width: Int, squareSize: Int) = width / 2 - squareSize / 2
    private fun getUpperLeftCornerY(height: Int, squareSize: Int) = height / 2 - squareSize / 2

    companion object {
        const val TARGET_SIZE = 64
    }
}

fun MultipartFile.getExtension(): String = FilenameUtils.getExtension(originalFilename!!)
fun MultipartFile.generateFileName(userId: ObjectId) = "$userId.${getExtension()}"
fun MultipartFile.generateFileName(userId: ObjectId, mediaId: ObjectId) = "$userId-$mediaId.${getExtension()}"

fun MultipartFile.toFile(userId: ObjectId, mediaId: ObjectId): File {
    val fileName = generateFileName(userId, mediaId)
    val data = BinaryData.fromBytes(bytes)
    return File(data, fileName)
}
