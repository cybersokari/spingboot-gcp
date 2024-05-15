package ng.cove.web.data.repo

import ng.cove.web.data.model.LevyPayment
import org.springframework.data.mongodb.repository.MongoRepository

interface LevyPaymentRepo : MongoRepository<LevyPayment, String>