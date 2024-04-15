package co.gatedaccess.web.repo;

import co.gatedaccess.web.model.SecurityGuardOtp;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SecurityGuardOtpRepo extends MongoRepository<SecurityGuardOtp, String> {
    SecurityGuardOtp findByCode(String code);
    SecurityGuardOtp findByCommunityId(String communityId);
    void deleteByCommunityId(String communityId);
}
