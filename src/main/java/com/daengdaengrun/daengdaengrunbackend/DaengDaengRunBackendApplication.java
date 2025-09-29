package com.daengdaengrun.daengdaengrunbackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing //JPA AUDITING 기능을 활성화한다.
@SpringBootApplication
public class DaengDaengRunBackendApplication {

    public static void main(String[] args) {
        // .env 파일을 로드해서 시스템 프로퍼티(System properties)로 설정합니다.
        Dotenv dotenv = Dotenv.configure().systemProperties().load();

        SpringApplication.run(DaengDaengRunBackendApplication.class, args);
    }

}
