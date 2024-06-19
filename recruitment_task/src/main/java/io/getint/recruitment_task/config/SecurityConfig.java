package io.getint.recruitment_task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/api/jira/sync").permitAll()  // Allow unauthenticated access to this endpoint
                .anyRequest().authenticated()
                .and()
                .csrf().disable();  // Disable CSRF for simplicity, consider the security implications in a real application

        return http.build();
    }
}
