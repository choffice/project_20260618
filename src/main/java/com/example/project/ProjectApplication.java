package com.example.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectApplication.class, args);
		System.out.println("제 이름은 권오창, 투잡이죠.");
		System.out.println("제 이름은 신창엽, 탕점이죠.");
		System.out.println("제 이름은 신창섭, 정상화의 신이죠.");
		System.out.println("제 이름은 이관언, 팀장이죠.");
		System.out.println("제 이름은 송원서, 탄창이죠.");
	}
}