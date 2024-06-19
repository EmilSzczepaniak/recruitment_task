package io.getint.recruitment_task.controller;

import io.getint.recruitment_task.synchronizer.JiraSynchronizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/jira")
public class JiraController {
    private static final Logger logger = LoggerFactory.getLogger(JiraController.class);
    private final JiraSynchronizer jiraSynchronizer;

    @Autowired
    public JiraController(JiraSynchronizer jiraSynchronizer) {
        this.jiraSynchronizer = jiraSynchronizer;
    }

    @GetMapping("/sync")
    public ResponseEntity<String> syncIssues() {
        try {
            jiraSynchronizer.moveTasksToOtherProject();
            return ResponseEntity.ok("Issues synchronized successfully");
        } catch (Exception e) {
            logger.error("Error synchronizing issues", e);
            return ResponseEntity.status(500).body("Error synchronizing issues: " + e.getMessage() + " - " + e.getCause());
        }
    }
}
