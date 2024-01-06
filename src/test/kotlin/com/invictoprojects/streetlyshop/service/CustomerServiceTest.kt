package com.invictoprojects.streetlyshop.service

import com.azure.core.util.BinaryData
import com.invictoprojects.streetlyshop.persistence.CustomerRepository
import com.invictoprojects.streetlyshop.persistence.ProductRepository
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Customer
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Gender
import com.invictoprojects.streetlyshop.persistence.domain.model.customer.Role
import com.invictoprojects.streetlyshop.service.model.File
import com.invictoprojects.streetlyshop.web.controller.request.UpdateCustomerDetailsRequest
import com.invictoprojects.streetlyshop.web.exception.UserAlreadyRegisteredException
import com.invictoprojects.streetlyshop.web.exception.UserNotFoundException
import org.assertj.core.api.Assertions.catchThrowable
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Instant

@ExtendWith(MockitoExtension::class)
internal class CustomerServiceTest {

    @Mock
    lateinit var customerRepository: CustomerRepository

    @Mock
    lateinit var passwordEncoder: PasswordEncoder

    @Mock
    lateinit var productRepository: ProductRepository

    @Mock
    lateinit var imageService: ImageService

    @Mock
    lateinit var fileService: FileService

    @InjectMocks
    lateinit var customerService: CustomerService

    @Test
    fun loadUserByUsername_userIsNotFound_userNotFoundExceptionIsThrown() {
        given(customerRepository.getByEmail("invalid@gmail.com")).willThrow(UserNotFoundException("error"))

        val throwable = catchThrowable { customerService.loadUserByUsername("invalid@gmail.com") }

        assertThat(throwable).isInstanceOf(UserNotFoundException::class.java)
    }

    @Test
    fun loadUserByUsername_userFound_userIsReturned() {
        val customer = getCustomer()
        given(customerRepository.getByEmail("john@gmail.com")).willReturn(customer)

        val actualCustomer = customerService.loadUserByUsername("john@gmail.com")

        assertThat(actualCustomer.username).isEqualTo(customer.id!!.toString())
        assertThat(actualCustomer.password).isEqualTo("password")
        assertThat(actualCustomer.authorities).contains(SimpleGrantedAuthority("ROLE_BUYER"))
        assertThat(actualCustomer.authorities).contains(SimpleGrantedAuthority("ROLE_SELLER"))
    }

    @Test
    fun register_emailIsTaken_userAlreadyRegisteredExceptionIsThrown() {
        given(customerRepository.findByEmail("john@gmail.com")).willReturn(getCustomer())

        val throwable = catchThrowable { customerService.register("john@gmail.com", "password") }

        assertThat(throwable).isInstanceOf(UserAlreadyRegisteredException::class.java)
    }

    @Test
    fun register_emailIsValid_userIsRegistered() {
        val email = "john@gmail.com"
        given(customerRepository.findByEmail(email)).willReturn(null)
        given(passwordEncoder.encode("password")).willReturn("encoded")

        customerService.register(email, "password")

        val expectedCustomer = Customer(
            nickname = email,
            email = email,
            password = "encoded",
            roles = mutableListOf(Role.BUYER, Role.SELLER)
        )
        verify(customerRepository).save(expectedCustomer)
    }

    @Test
    fun updateAvatar_userIsNotFound_userNotFoundExceptionIsThrown() {
        val userId = ObjectId()
        given(customerRepository.getById(userId)).willThrow(UserNotFoundException("error"))

        val file = MockMultipartFile("avatar.jpg", "Image".toByteArray())
        val throwable = catchThrowable { customerService.updateAvatar(file, userId) }

        assertThat(throwable).isInstanceOf(UserNotFoundException::class.java)
        verifyNoInteractions(imageService)
        verifyNoInteractions(fileService)
        verify(customerRepository, never()).save(any())
    }

    @Test
    fun updateAvatar_imageServiceThrowsException_exceptionIsThrown() {
        val customer = getCustomer()
        val userId = customer.id!!
        given(customerRepository.getById(userId)).willReturn(customer)

        val file = MockMultipartFile("avatar.jpg", "Image".toByteArray())
        given(imageService.toAvatar(file, userId)).willThrow(RuntimeException())

        val throwable = catchThrowable { customerService.updateAvatar(file, userId) }

        assertThat(throwable).isInstanceOf(RuntimeException::class.java)
        verifyNoInteractions(fileService)
        verify(customerRepository, never()).save(any())
    }

    @Test
    fun updateAvatar_fileServiceThrowsException_exceptionIsThrown() {
        val customer = getCustomer()
        val userId = customer.id!!
        given(customerRepository.getById(userId)).willReturn(customer)

        val file = MockMultipartFile("avatar.jpg", "Image".toByteArray())
        val avatar = File(BinaryData.fromString("avatar"), "$userId.jpg")

        given(imageService.toAvatar(file, userId)).willReturn(avatar)
        given(fileService.uploadFile(avatar, AVATARS_CONTAINER)).willThrow(RuntimeException())

        val throwable = catchThrowable { customerService.updateAvatar(file, userId) }

        assertThat(throwable).isInstanceOf(RuntimeException::class.java)
        verify(customerRepository, never()).save(any())
    }

    @Test
    fun updateAvatar_customerRepositoryThrowsException_exceptionIsThrown() {
        val customer = getCustomer()
        val userId = customer.id!!
        given(customerRepository.getById(userId)).willReturn(customer)

        val file = MockMultipartFile("avatar.jpg", "Image".toByteArray())
        val avatar = File(BinaryData.fromString("avatar"), "$userId.jpg")

        given(imageService.toAvatar(file, userId)).willReturn(avatar)
        given(fileService.uploadFile(avatar, AVATARS_CONTAINER)).willReturn("https://$userId.jpg")
        given(customerRepository.save(any())).willThrow(RuntimeException())

        val throwable = catchThrowable { customerService.updateAvatar(file, userId) }

        assertThat(throwable).isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun updateAvatar_fileServiceThrowsExceptionWhileDeletingOldAvatar_exceptionIsThrown() {
        val customer = getCustomer()
        val userId = customer.id!!
        customer.avatar = "https://monomarketstorage.blob.core.windows.net/avatars/$userId.jpg"
        given(customerRepository.getById(userId)).willReturn(customer)

        val file = MockMultipartFile("avatar.png", "Image".toByteArray())
        val avatar = File(BinaryData.fromString("avatar"), "$userId.png")

        given(imageService.toAvatar(file, userId)).willReturn(avatar)
        given(fileService.uploadFile(avatar, AVATARS_CONTAINER)).willReturn("https://$userId.png")
        given(fileService.deleteFile("$userId.jpg", AVATARS_CONTAINER)).willThrow(RuntimeException())

        val throwable = catchThrowable { customerService.updateAvatar(file, userId) }

        assertThat(throwable).isInstanceOf(RuntimeException::class.java)
    }

    @Test
    fun updateAvatar_fileNameChanged_imageIsUploaded() {
        val customer = getCustomer()
        val userId = customer.id!!
        customer.avatar = "https://monomarketstorage.blob.core.windows.net/avatars/$userId.jpg"
        given(customerRepository.getById(userId)).willReturn(customer)

        val file = MockMultipartFile("avatar.png", "Image".toByteArray())
        val avatar = File(BinaryData.fromString("avatar"), "$userId.png")

        given(imageService.toAvatar(file, userId)).willReturn(avatar)
        given(fileService.uploadFile(avatar, AVATARS_CONTAINER)).willReturn("https://$userId.png")

        val response = customerService.updateAvatar(file, userId)

        val expectedCustomer = Customer(
            id = customer.id,
            email = customer.email,
            password = customer.password,
            nickname = customer.nickname,
            roles = customer.roles,
            avatar = "https://$userId.png"
        )
        verify(customerRepository).save(expectedCustomer)
        verify(fileService).deleteFile("$userId.jpg", AVATARS_CONTAINER)
        assertThat(response.imageUrl).isEqualTo("https://$userId.png")
    }

    @Test
    fun updateDetails_requestIsValid_customerIsUpdated() {
        val customer = getCustomer()
        val userId = customer.id!!
        given(customerRepository.getById(userId)).willReturn(customer)

        val birthDay = Instant.now().toEpochMilli()
        val request = UpdateCustomerDetailsRequest(
            phone = "380991234567",
            birthDay = birthDay,
            name = "Vlad",
            surname = "Smith",
            middleName = "Black",
            gender = Gender.MALE,
            nickname = "UserVlad"
        )

        customerService.updateDetails(userId, request)

        val expectedCustomer = Customer(
            id = userId,
            email = customer.email,
            password = customer.password,
            nickname = request.nickname!!,
            roles = customer.roles,
            phone = request.phone,
            birthDay = request.birthDay,
            name = request.name,
            surname = request.surname,
            middleName = request.middleName,
            gender = request.gender,
        )
        verify(customerRepository).save(expectedCustomer)
    }

    @Test
    fun addFavoriteProduct_productIsAlreadyFavorite_noWriteOperation() {
        val productId = ObjectId()
        val customer = getCustomer()

        customer.favoriteProductIds.add(productId)
        given(customerRepository.getById(customer.id!!)).willReturn(customer)

        customerService.addFavoriteProduct(customer.id!!.toString(), productId.toString())

        verify(customerRepository, never()).addFavoriteProduct(any(), any())
        verify(productRepository, never()).increaseFavoriteCount(any())
    }

    @Test
    fun addFavoriteProduct_productIsNotFavorite_favoriteProductIsAdded() {
        val productId = ObjectId()
        val customer = getCustomer()

        given(customerRepository.getById(customer.id!!)).willReturn(customer)

        customerService.addFavoriteProduct(customer.id!!.toString(), productId.toString())

        verify(customerRepository).addFavoriteProduct(customer.id!!, productId)
        verify(productRepository).increaseFavoriteCount(productId)
    }

    @Test
    fun removeFavoriteProduct_productIsFavorite_favoriteProductIsRemoved() {
        val productId = ObjectId()
        val customer = getCustomer()

        customer.favoriteProductIds.add(productId)
        given(customerRepository.getById(customer.id!!)).willReturn(customer)

        customerService.removeFavoriteProduct(customer.id!!.toString(), productId.toString())

        verify(customerRepository).removeFavoriteProduct(customer.id!!, productId)
        verify(productRepository).decreaseFavoriteCount(productId)
    }

    @Test
    fun removeFavoriteProduct_productIsNotFavorite_noWriteOperation() {
        val productId = ObjectId()
        val customer = getCustomer()

        given(customerRepository.getById(customer.id!!)).willReturn(customer)

        customerService.removeFavoriteProduct(customer.id!!.toString(), productId.toString())

        verify(customerRepository, never()).removeFavoriteProduct(any(), any())
        verify(productRepository, never()).decreaseFavoriteCount(any())
    }

    private fun getCustomer() = Customer(
        id = ObjectId(),
        email = "john@gmail.com",
        password = "password",
        nickname = "john@gmail.com",
        roles = mutableListOf(Role.BUYER, Role.SELLER)
    )

    private fun <T> any(): T = Mockito.any()
}