package co.gatedaccess.web.data.repo;

import co.gatedaccess.web.data.model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRepo extends MongoRepository<Device, String> {
    Device findDeviceByMemberId(String memberId);
}
