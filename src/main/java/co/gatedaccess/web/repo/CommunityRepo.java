package co.gatedaccess.web.repo;

import co.gatedaccess.web.model.Community;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommunityRepo extends MongoRepository<Community, String> {

    Community getById(String id);
    List<Community> searchCommunitiesByNameContainingIgnoreCase(String name);
}
