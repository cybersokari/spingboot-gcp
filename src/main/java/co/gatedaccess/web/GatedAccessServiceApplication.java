package co.gatedaccess.web;

import co.gatedaccess.web.model.Community;
import co.gatedaccess.web.model.Member;
import co.gatedaccess.web.service.CommunityService;
import com.google.firebase.FirebaseApp;
import com.mongodb.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.*;

//@RequestMapping("/v1") Dont use it, it will break the '/secure' path interceptor
//@EnableMongoAuditing
@SpringBootApplication
@EnableMongoRepositories("co.gatedaccess.web.repo")
@RestController
public class GatedAccessServiceApplication {

    @Autowired
    private CommunityService communityService;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GatedAccessServiceApplication.class);
        app.addListeners((ApplicationListener<ApplicationStartedEvent>) event -> FirebaseApp.initializeApp());
        app.run(args);
    }


    @PostMapping("/secure/community/join")
    ResponseEntity<String> requestToJoinCommunity(@RequestAttribute("user") String userId,
                                                  @RequestParam("invite-code") String inviteCode) {
        return communityService.join(inviteCode, userId);
    }

    @PostMapping("/secure/community/request/{request-id}")
    ResponseEntity<String> handleMemberRequest(@RequestAttribute("user") String userId,
                                               @PathVariable("request-id") String requestId,
                                               @RequestParam Boolean accept) {
        return communityService.handleMemberRequest(userId, requestId, accept);
    }

    @PostMapping("community/{id}/admin")
    ResponseEntity<Community> updateCommunityAdmin(@PathVariable String id, @RequestBody Member body) {
        return communityService.updateSuperAdmin(id, body);
    }

    @GetMapping("/guard-login/{otp}")
    ResponseEntity<?> loginSecurityGuard(@PathVariable String otp, @RequestHeader("x-device-name") String device) {
        return communityService.getCustomTokenForSecurityGuard(otp, device);
    }

//    @GetMapping("/secure/guard-otp/create")
//    ResponseEntity<?> getLoginOtpForSecurityGuard(@RequestAttribute("user") String adminUserId) {
//
//    }


    // Exception handler for MissingRequestHeaderException
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingHeader(@NonNull MissingRequestHeaderException ex) {
        return ResponseEntity.badRequest().body("Required header '" + ex.getHeaderName() + "' is missing.");
    }
}
