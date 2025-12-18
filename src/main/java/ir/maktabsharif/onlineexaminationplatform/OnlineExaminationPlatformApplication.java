package ir.maktabsharif.onlineexaminationplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class OnlineExaminationPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineExaminationPlatformApplication.class, args);
    }

}
