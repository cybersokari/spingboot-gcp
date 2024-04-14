package co.gatedaccess.web.repo;

import co.gatedaccess.web.model.Occupant;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OccupantRepo extends MongoRepository<Occupant, String> {
    Occupant findByGuardianCommunity_IdAndFirstNameOrLastNameContainingIgnoreCase(String id, String s1, String s2);
}
