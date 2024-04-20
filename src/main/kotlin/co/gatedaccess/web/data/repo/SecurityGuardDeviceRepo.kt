package co.gatedaccess.web.data.repo

import co.gatedaccess.web.data.model.SecurityGuardDevice
import org.springframework.data.mongodb.repository.MongoRepository

interface SecurityGuardDeviceRepo : MongoRepository<SecurityGuardDevice?, String?>
