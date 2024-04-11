package co.gatedaccess.web;

import co.gatedaccess.web.model.Community;
import co.gatedaccess.web.repo.CommunityRepo;
import com.google.firebase.FirebaseApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
@RequestMapping("/v1")
@EnableMongoRepositories
public class GatedAccessServiceApplication {

    @Autowired
    private CommunityRepo communityRepo;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GatedAccessServiceApplication.class);
        app.addListeners((ApplicationListener<ApplicationStartedEvent>) event -> FirebaseApp.initializeApp());
        app.run(args);
    }

    @GetMapping("/secure/access")
    String getAccessCode(@RequestAttribute("user") String userId) {
        System.out.println("This is a user: " + userId);
        return "";
    }

    @PostMapping("secure/resident/join")
    String joinCommunity(@RequestAttribute("user") String userId, @RequestBody() String communityId) {
        System.out.println("This is a user: nullable");
        return "";
    }

    @GetMapping("/community/{id}")
    Community getById(@PathVariable String id){
        return communityRepo.getById(id);
    }

    @GetMapping("/community")
    List<Community> getCommunitiesByName(@RequestParam String name){
        return communityRepo.searchCommunitiesByNameContainingIgnoreCase(name);
    }
}
