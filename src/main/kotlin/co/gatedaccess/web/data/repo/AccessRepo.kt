package co.gatedaccess.web.data.repo

import co.gatedaccess.web.data.model.Access
import co.gatedaccess.web.data.model.AccessId
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface AccessRepo: MongoRepository<Access, AccessId>{
    fun findByIdAndValidUntilIsAfter(id: AccessId, validUntil: Date): Access?
}