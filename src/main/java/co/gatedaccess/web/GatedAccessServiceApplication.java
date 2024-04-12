package co.gatedaccess.web;

import co.gatedaccess.web.http.JoinBody;
import co.gatedaccess.web.model.Community;
import co.gatedaccess.web.model.Member;
import co.gatedaccess.web.service.CommunityService;
import com.google.firebase.FirebaseApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//@RequestMapping("/v1") Dont use it, it will break the '/secure' path interceptor
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


    @PostMapping("/secure/join/community")
    ResponseEntity<String> requestToJoinCommunity(@RequestAttribute("user") String userId,
                                                  @RequestBody JoinBody body) {
        return communityService.join(body, userId);
    }

    @PostMapping("/secure/community/request/{id}")
    ResponseEntity<String> handleMemberRequest(@RequestAttribute("user") String userId,
                                               @RequestParam Boolean accept,
                                               @PathVariable String id) {
        return communityService.handleMemberRequest(userId, id, accept);
    }

    @PostMapping("community/{id}/admin")
    ResponseEntity<Community> updateCommunityAdmin(@PathVariable String id, @RequestBody Member body) {
        return communityService.updateSuperAdmin(id, body);
    }
}
