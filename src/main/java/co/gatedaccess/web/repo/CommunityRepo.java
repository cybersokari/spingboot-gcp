package co.gatedaccess.web.repo;

import co.gatedaccess.web.model.Community;
import org.springframework.data.repository.CrudRepository;

public interface CommunityRepo extends CrudRepository<Community, String> {

    Community findCommunityById(String id);

    Community findCommunityBySuperAdminId(String id);
}
