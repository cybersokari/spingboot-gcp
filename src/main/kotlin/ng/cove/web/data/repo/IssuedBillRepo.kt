package ng.cove.web.data.repo

import ng.cove.web.data.model.IssuedBill
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface IssuedBillRepo : MongoRepository<IssuedBill?,String>{
    fun findAllByNextPaymentDueIsBeforeOrderByNextPaymentDueAsc(today: Date): List<IssuedBill>
}