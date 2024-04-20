package co.gatedaccess.web.data.repo

import co.gatedaccess.web.data.model.Device
import org.springframework.data.mongodb.repository.MongoRepository

interface DeviceRepo : MongoRepository<Device?, String?> {
    fun findDeviceByMemberId(memberId: String?): Device?
}
