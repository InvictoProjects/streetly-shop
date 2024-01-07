package com.invictoprojects.streetlyshop.service

import com.invictoprojects.streetlyshop.persistence.CustomerRepository
import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Role
import com.invictoprojects.streetlyshop.persistence.impl.toObjectId
import com.invictoprojects.streetlyshop.web.controller.request.UpdateCustomerDetailsRequest
import com.invictoprojects.streetlyshop.web.controller.response.ImageUploadResponse
import com.invictoprojects.streetlyshop.web.exception.UserAlreadyRegisteredException
import org.bson.types.ObjectId
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

const val AVATARS_CONTAINER = "avatars"

@Service
class CustomerService(
    val customerRepository: CustomerRepository,
    val productRepository: ProductRepository,
    val passwordEncoder: PasswordEncoder,
    val imageService: ImageService,
    val fileService: FileService
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val customer = customerRepository.getByEmail(email)
        return User(customer.id!!.toString(), customer.password, customer.roles.map { SimpleGrantedAuthority(it.role) })
    }

    fun register(email: String, rawPassword: String) {
        if (customerRepository.findByEmail(email) != null) throw UserAlreadyRegisteredException(email)
        customerRepository.save(getCustomer(email, rawPassword))
    }

    fun updateAvatar(file: MultipartFile, userId: ObjectId): ImageUploadResponse {
        val customer = customerRepository.getById(userId)

        val oldAvatarFileName = customer.getAvatarFileName()
        val newAvatar = imageService.toAvatar(file, userId)
        val newAvatarUrl = fileService.uploadFile(newAvatar, AVATARS_CONTAINER)

        customer.avatar = newAvatarUrl
        customerRepository.save(customer)

        if (hasFileNameChanged(oldAvatarFileName, newAvatar.name)) {
            fileService.deleteFile(oldAvatarFileName!!, AVATARS_CONTAINER)
        }
        return ImageUploadResponse(newAvatarUrl)
    }

    fun updateDetails(userId: ObjectId, updateRequest: UpdateCustomerDetailsRequest) {
        val customer = customerRepository.getById(userId)

        with(updateRequest) {
            name?.let { customer.name = it }
            surname?.let { customer.surname = it }
            middleName?.let { customer.middleName = it }
            phone?.let { customer.phone = it }
            birthDay?.let { customer.birthDay = it }
            gender?.let { customer.gender = it }
            nickname?.let { customer.nickname = it }
        }

        customerRepository.save(customer)
    }

    private fun hasFileNameChanged(oldAvatarFileName: String?, newAvatarFileName: String): Boolean {
        return oldAvatarFileName != null && oldAvatarFileName != newAvatarFileName
    }

    private fun getCustomer(email: String, rawPassword: String) =
        Customer(
            email = email,
            nickname = email,
            password = passwordEncoder.encode(rawPassword),
            roles = mutableListOf(Role.BUYER, Role.SELLER)
        )

    fun addFavoriteProduct(userId: String, productIdString: String) {
        val user = customerRepository.getById(userId.toObjectId())
        val productId = productIdString.toObjectId()

        if (!user.favoriteProductIds.contains(productId)) {
            customerRepository.addFavoriteProduct(user.id!!, productId)
            productRepository.increaseFavoriteCount(productId)
        }
    }

    fun removeFavoriteProduct(userId: String, productIdString: String) {
        val user = customerRepository.getById(userId.toObjectId())
        val productId = productIdString.toObjectId()

        if (user.favoriteProductIds.contains(productId)) {
            customerRepository.removeFavoriteProduct(user.id!!, productId)
            productRepository.decreaseFavoriteCount(productId)
        }
    }
}
