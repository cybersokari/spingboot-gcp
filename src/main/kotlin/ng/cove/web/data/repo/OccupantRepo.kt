package ng.cove.web.data.repo

import ng.cove.web.data.model.Occupant
import org.springframework.data.mongodb.repository.MongoRepository

interface OccupantRepo : MongoRepository<Occupant?, String?> {
    fun findByGuardianCommunityIdAndFirstNameOrLastNameContainingIgnoreCase(
        id: String?,
        s1: String?,
        s2: String?
    ): Occupant?
}
