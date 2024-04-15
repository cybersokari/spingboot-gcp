package co.gatedaccess.web.service;

import co.gatedaccess.web.http.GuardOtpBody;
import co.gatedaccess.web.http.TokenBody;
import co.gatedaccess.web.model.*;
import co.gatedaccess.web.repo.*;
import co.gatedaccess.web.util.ApiResponseMessage;
import co.gatedaccess.web.util.CodeGenerator;
import co.gatedaccess.web.util.CodeType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Date;
import java.util.Objects;

@Component
public class CommunityService {

    @Autowired
    CommunityRepo communityRepo;
    @Autowired
    MemberRepo memberRepo;
    @Autowired
    JoinCommunityRequestRepo joinCommunityRequestRepo;
    @Autowired
    SecurityGuardOtpRepo securityGuardOtpRepo;
    @Autowired
    SecurityGuardDeviceRepo securityGuardDeviceRepo;
    @Autowired
    Environment environment;

    @Transactional
    public ResponseEntity<TokenBody> getCustomTokenForSecurityGuard(String otp, String deviceName) {

        SecurityGuardOtp securityGuardOtp = securityGuardOtpRepo.findByCode(otp);
        if (securityGuardOtp == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new TokenBody().setErrorMessage("Invalid Otp"));
        }

        LocalDateTime otpCreatedDate = securityGuardOtp.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Duration duration = Duration.between(otpCreatedDate, LocalDateTime.now());

        if (duration.toMinutes() > 5) {
            securityGuardOtpRepo.delete(securityGuardOtp);
            return ResponseEntity.status(HttpStatus.GONE).body(new TokenBody().setErrorMessage("Otp Expired"));
        }

        SecurityGuardDevice device = new SecurityGuardDevice
                .Builder()
                .withDeviceName(deviceName)
                .withCommunityId(securityGuardOtp.getCommunityId()).build();
        device = securityGuardDeviceRepo.save(device);
        try {
            String customToken = FirebaseAuth.getInstance().createCustomToken(device.getId());
            return ResponseEntity.ok().body(new TokenBody(customToken));

        } catch (FirebaseAuthException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new TokenBody().setErrorMessage(e.getLocalizedMessage()));
        }
    }

    @Transactional
    public ResponseEntity<GuardOtpBody> getSecurityGuardOtpForAdmin(String adminUserId){
        Community community = communityRepo.findCommunityBySuperAdminId(adminUserId);
        if(community == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        // Delete old before creating a new one
        securityGuardOtpRepo.deleteByCommunityId(community.getId());


        // Retry otp generation if any duplicate occur
        Date expiryTime = null;
        SecurityGuardOtp guardOtp = new SecurityGuardOtp();
        int codeExpiryDurationInSecs = Integer.parseInt(Objects.requireNonNull(environment.getProperty("security-guard.otp.duration-in-secs")));
        while (guardOtp.getId() == null){// Check for MongoDb ID to know if save is successful

            guardOtp.setCode(new CodeGenerator(CodeType.guard).getCode());

            LocalDateTime futureDateTime = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            futureDateTime = futureDateTime.plusSeconds(codeExpiryDurationInSecs);
            expiryTime = Date.from(Instant.from(futureDateTime));
            guardOtp.setExpireAt(expiryTime);

            try {
                guardOtp = securityGuardOtpRepo.save(guardOtp);
            }catch (DuplicateKeyException ignored){}
        }
        return ResponseEntity.ok(new GuardOtpBody(guardOtp.getCode(), expiryTime));

    }

    /**
     * @param adminUserId Identity of the admin of the community
     * @param requestId
     * @param accept      if the request was accepted or rejected
     * @return status of the request
     */
    @Transactional
    public ResponseEntity<String> handleMemberRequest(String adminUserId, String requestId, Boolean accept) {
        Community community = communityRepo.findCommunityBySuperAdminId(adminUserId);
        if (community == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseMessage.USER_NOT_SUPER_ADMIN);
        }
        JoinCommunityRequest request = joinCommunityRequestRepo.findJoinCommunityRequestById(requestId);
        if (request == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseMessage.REQUEST_CANT_BE_FOUND);
        }
        String memberId = request.getMember().getId();
        if (joinCommunityRequestRepo.existsJoinCommunityRequestByMember_IdAndAcceptedAtIsNotNull(memberId)) {
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(ApiResponseMessage.REQUEST_ALREADY_ACCEPTED);
        }
        if (accept) {
            Member member = request.getMember();
            member.setCommunity(community);
            memberRepo.save(member);
            updateMemberInviteCode(member);

            request.setAcceptedAt(new Date());
        } else {
            request.setRejectAt(new Date());
        }
        joinCommunityRequestRepo.save(request);

        return ResponseEntity.status(HttpStatus.OK).body("Request updated");
    }

    /**
     * Recursively try to update users invite code if the update fails
     *
     * @param member
     */
    private void updateMemberInviteCode(Member member) {
        try {
            member.setInviteCode(new CodeGenerator(CodeType.visitor).getCode());
            memberRepo.save(member);
        } catch (DuplicateKeyException e) {
            updateMemberInviteCode(member);
        }
    }

    public ResponseEntity<String> join(String inviteCode, String userId) {

        try {
            Member referrer = memberRepo.findMemberByInviteCode(inviteCode);
            if (referrer == null) {
                return ResponseEntity.badRequest().body("Invite code is not valid");
            }

            Community community = referrer.getCommunity();
            if (community == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This user is part of a community");
            }

            if (community.getSuperAdmin() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Community does not have an Admin");
            }

            Member member = memberRepo.findMemberById(userId);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Create user entity with client app SDK");
            }
            if (member.getCommunity() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already part of a community");
            }
            if (member.getPhotoUrl() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponseMessage.PHOTO_IS_REQUIRED);
            }

            if (joinCommunityRequestRepo.findJoinCommunityRequestByMember_IdAndAcceptedAtIsNull(userId) != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("User already have a pending request that has not been accepted");
            }

            JoinCommunityRequest request = new JoinCommunityRequest.Builder()
                    .withMember(member)
                    .withReferrer(referrer)
                    .withCommunity(community).build();

            joinCommunityRequestRepo.save(request);

            return ResponseEntity.status(HttpStatus.OK)
                    .body("Request to join community sent");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getLocalizedMessage());
        }
    }
}
