package com.invictoprojects.streetlyshop.persistence

import com.invictoprojects.streetlyshop.persistence.domain.model.product.variant.price.ExchangeRate
import org.springframework.data.mongodb.repository.MongoRepository

interface ExchangeRateRepository : MongoRepository<ExchangeRate, String>
