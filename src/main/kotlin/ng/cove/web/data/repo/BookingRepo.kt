package ng.cove.web.data.repo

import ng.cove.web.data.model.Booking
import org.springframework.data.mongodb.repository.MongoRepository

interface BookingRepo: MongoRepository<Booking, String>{
    fun findByCodeAndCommunityId(code: String, communityId: String): Booking?
    fun findByCodeAndCommunityIdAndCheckedInAtIsNotNull(code: String, communityId: String): Booking?
}