package info.ieathealthy.restcontrollers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;


@SpringBootApplication
//load MongoClient bean which can be used for dependency injection
@ImportResource({"file:src/main/java/info/ieathealthy/mongoutils/mongocodecregistry.xml", "file:src/main/java/info/ieathealthy/restcontrollers/mongoclient.xml"})
public class Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.run(args);
    }
}