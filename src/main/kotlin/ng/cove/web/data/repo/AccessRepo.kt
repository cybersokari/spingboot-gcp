package ng.cove.web.data.repo

import ng.cove.web.data.model.Access
import ng.cove.web.data.model.AccessId
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface AccessRepo: MongoRepository<Access, AccessId>{
    fun findByIdAndValidUntilIsAfter(id: AccessId, validUntil: Date): Access?
}