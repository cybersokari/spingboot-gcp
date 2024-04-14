package co.gatedaccess.web.repo;

import co.gatedaccess.web.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRepo extends MongoRepository<Device, String> {
    Device findDeviceByMemberId(String memberId);
}
