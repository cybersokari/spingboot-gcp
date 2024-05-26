package ng.cove.web.data.repo

import ng.cove.web.data.model.Bill
import org.springframework.data.mongodb.repository.MongoRepository

interface BillRepo : MongoRepository<Bill, String>