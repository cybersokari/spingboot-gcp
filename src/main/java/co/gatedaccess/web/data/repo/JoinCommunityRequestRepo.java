package co.gatedaccess.web.data.repo;

import co.gatedaccess.web.data.model.JoinCommunityRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JoinCommunityRequestRepo extends MongoRepository<JoinCommunityRequest, String> {

    JoinCommunityRequest findJoinCommunityRequestByMember_IdAndAcceptedAtIsNull(String memberId);
    Boolean existsJoinCommunityRequestByMember_IdAndAcceptedAtIsNotNull(String memberId);
    JoinCommunityRequest findJoinCommunityRequestById(String id);
}
