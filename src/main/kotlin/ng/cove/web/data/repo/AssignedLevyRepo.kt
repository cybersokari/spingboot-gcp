package ng.cove.web.data.repo

import ng.cove.web.data.model.AssignedLevy
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface AssignedLevyRepo : MongoRepository<AssignedLevy?,String?>{
    fun findAllByNextPaymentDueIsBeforeOrderByNextPaymentDueAsc(today: Date): List<AssignedLevy>
}