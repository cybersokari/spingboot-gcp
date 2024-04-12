package co.gatedaccess.web.service;

import co.gatedaccess.web.http.JoinBody;
import co.gatedaccess.web.model.Community;
import co.gatedaccess.web.model.JoinCommunityRequest;
import co.gatedaccess.web.model.Member;
import co.gatedaccess.web.repo.CommunityRepo;
import co.gatedaccess.web.repo.JoinCommunityRequestRepo;
import co.gatedaccess.web.repo.MemberRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CommunityService {

    @Autowired
    CommunityRepo communityRepo;
    @Autowired
    MemberRepo memberRepo;
    @Autowired
    JoinCommunityRequestRepo joinCommunityRequestRepo;

    public ResponseEntity<Community> updateSuperAdmin(String communityId, Member member) {
        Community community = communityRepo.findCommunityById(communityId);
        System.out.println(community);

        community.setSuperAdmin(member);

        community = communityRepo.save(community);
        return ResponseEntity.status(HttpStatus.OK).body(community);
    }

    public ResponseEntity<String> handleMemberRequest(String adminUserId, String requestId, Boolean accept) {
        Community community = communityRepo.findCommunityBySuperAdminId(adminUserId);
        if (community == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not an Admin");
        }
        JoinCommunityRequest request = joinCommunityRequestRepo.findJoinCommunityRequestById(requestId);
        if (request == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Request does not exist");
        }
        String memberId = request.getMember().getId();
        if (joinCommunityRequestRepo.existsJoinCommunityRequestByMember_IdAndAcceptedAtIsNotNull(memberId)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body("Request has already been accepted");
        }
        if (accept) {
            Member member = request.getMember();
            member.setCommunity(community);
            memberRepo.save(member);

            request.setAcceptedAt(new Date());
        } else {
            request.setRejectAt(new Date());
        }

        joinCommunityRequestRepo.save(request);
        return ResponseEntity.status(HttpStatus.OK).body("Request updated");

//        MongoClient client = MongoClients.create();
//        ClientSession session = client.startSession();
//        session.startTransaction();
//        session.commitTransaction();
//        session.close();
//        client.close();
    }

    public ResponseEntity<String> join(JoinBody body, String userId) {

        try {
            Community community = communityRepo.findCommunityById(body.getCommunityId());
            if (community == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Community cannot be found");
            }

            System.out.println(community);
            if (community.getSuperAdmin() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Community does not have an Admin");
            }

            if (!community.getSuperAdmin().getPhone().equalsIgnoreCase(body.getAdminPhone())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Phone number does not match Admin's");
            }
            Member member = memberRepo.findMemberById(userId);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Create user entity with client app SDK");
            }
            if (member.getCommunity() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Member is already part of a community");
            }

            if (joinCommunityRequestRepo.findJoinCommunityRequestByMember_IdAndAcceptedAtIsNull(userId) != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Member already have a pending request that has not been accepted");
            }

            JoinCommunityRequest request = new JoinCommunityRequest.Builder().withCommunity(community).withMember(member)
                    .withCreatedAt(new Date()).build();

            joinCommunityRequestRepo.save(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getLocalizedMessage());
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body("Request to join community sent");
    }
}
