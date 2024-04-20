package co.gatedaccess.web.data.repo;

import co.gatedaccess.web.data.model.Community;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommunityRepo extends MongoRepository<Community, String> {

    Community findCommunityById(String id);

    Community findCommunityBySuperAdminId(String id);
}
