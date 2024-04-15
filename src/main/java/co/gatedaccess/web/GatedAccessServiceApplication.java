package co.gatedaccess.web;

import co.gatedaccess.web.http.GuardOtpBody;
import co.gatedaccess.web.http.TokenBody;
import co.gatedaccess.web.service.CommunityService;
import co.gatedaccess.web.service.UserService;
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
@SpringBootApplication
@EnableMongoRepositories("co.gatedaccess.web.repo")
@RestController
public class GatedAccessServiceApplication {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private UserService userService;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GatedAccessServiceApplication.class);
        app.addListeners((ApplicationListener<ApplicationStartedEvent>) event ->/*init Firebase*/ FirebaseApp.initializeApp());
        app.run(args);
    }

    /**
     * Enable the client app to exchange Google/Apple provider token for a
     * custom Firebase token that can be used to authenticate the client app
     *
     * @param token
     * @param provider
     * @return A custom Firebase token
     */
    @GetMapping("/user/provider/{provider}/login")
    ResponseEntity<TokenBody> loginUserWithProvider(@RequestParam String token,
                                                     @PathVariable("provider") String provider) {
        return userService.getCustomTokenForClientLogin(token, provider);
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

    @GetMapping("/secure/guard-otp/create")
    ResponseEntity<GuardOtpBody> getSecurityGuardOtpForAdmin(@RequestAttribute("user") String adminUserId) {
        return communityService.getSecurityGuardOtpForAdmin(adminUserId);
    }

    @GetMapping("/guard-login/{otp}")
    ResponseEntity<TokenBody> loginSecurityGuard(@PathVariable String otp, @RequestHeader("x-device-name") String device) {
        return communityService.getCustomTokenForSecurityGuard(otp, device);
    }

    // Exception handler for MissingRequestHeaderException
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<String> handleMissingHeader(@NonNull MissingRequestHeaderException ex) {
        return ResponseEntity.badRequest().body("Required header '" + ex.getHeaderName() + "' is missing.");
    }

}
