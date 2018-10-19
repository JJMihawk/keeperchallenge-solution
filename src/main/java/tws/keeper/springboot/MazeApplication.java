package tws.keeper.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"tws.keeper.springboot"})
public class MazeApplication {
    public static void main(String[] args) {
        SpringApplication.run(MazeApplication.class, args);
    }
}
