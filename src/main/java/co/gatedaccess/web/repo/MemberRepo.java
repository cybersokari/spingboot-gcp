package co.gatedaccess.web.repo;

import co.gatedaccess.web.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemberRepo extends MongoRepository<Member, String> {
    Member findMemberById(String userId);

    Member findMemberByInviteCode(String inviteCode);

    Member findMemberByEmail(String email);

    Boolean existsMemberByEmail(String email);
}
