package ng.cove.web.data.repo

import ng.cove.web.data.model.Device
import org.springframework.data.mongodb.repository.MongoRepository

interface DeviceRepo : MongoRepository<Device?, String?> {
    fun findDeviceByMemberId(memberId: String?): Device?
}
