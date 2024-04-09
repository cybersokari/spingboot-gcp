package co.gatedaccess.web;

import com.google.firebase.FirebaseApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class GatedAccessServiceApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(GatedAccessServiceApplication.class);
        app.addListeners((ApplicationListener<ApplicationStartedEvent>) event -> {
            FirebaseApp.initializeApp();
        });

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


}
