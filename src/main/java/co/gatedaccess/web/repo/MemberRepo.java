package co.gatedaccess.web.repo;

import co.gatedaccess.web.model.Community;
import co.gatedaccess.web.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberRepo extends MongoRepository<Member, String> {
    Member findMemberById(String userId);
    Member findMemberByInviteCode(String inviteCode);
}
