package io.getint.recruitment_task.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = "jira")
@Data
public class JiraConfig {
    private String baseUrl;
    private String username;
    private String apiToken;
    private String sourceProjectKey;
    private String targetProjectKey;


    @PostConstruct
    public void init() {
        System.out.println("JiraConfig initialized with baseUrl=" + baseUrl + ", username=" + username + ", apiToken=" + apiToken + ", sourceProjectKey=" + sourceProjectKey + ", targetProjectKey=" + targetProjectKey);
    }
}
