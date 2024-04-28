package co.gatedaccess.web.data.repo

import co.gatedaccess.web.data.model.JoinRequest
import co.gatedaccess.web.data.model.JoinRequestId
import org.springframework.data.mongodb.repository.MongoRepository

interface JoinRequestRepo : MongoRepository<JoinRequest?, JoinRequestId?> {
    fun existsByIdAndAcceptedAtIsNull(id: JoinRequestId): Boolean
    fun findByIdPhone(phone: String): List<JoinRequest>?
    fun deleteAllByIdPhoneAndAcceptedAtIsNull(phone: String)
}
