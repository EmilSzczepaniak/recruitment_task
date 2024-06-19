package io.getint.recruitment_task;

import io.getint.recruitment_task.config.JiraConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JiraConfig.class)
public class RecruitmentTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitmentTaskApplication.class, args);
    }
}