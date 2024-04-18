package co.gatedaccess.web;

import com.google.firebase.FirebaseApp;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories("co.gatedaccess.web.data.repo")
public class App {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        app.addListeners((ApplicationListener<ApplicationStartedEvent>) event ->/*init Firebase*/ FirebaseApp.initializeApp());
        app.run(args);
    }

}
