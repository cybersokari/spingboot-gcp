package co.gatedaccess.web.repo;

import co.gatedaccess.web.model.SecurityGuardDevice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SecurityGuardDeviceRepo extends MongoRepository<SecurityGuardDevice, String> {
}
