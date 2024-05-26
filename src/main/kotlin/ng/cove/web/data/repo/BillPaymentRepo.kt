package ng.cove.web.data.repo

import ng.cove.web.data.model.BillPayment
import org.springframework.data.mongodb.repository.MongoRepository

interface BillPaymentRepo : MongoRepository<BillPayment, String>