package io.getint.recruitment_task.synchronizer;

import io.getint.recruitment_task.service.JiraService;
import io.getint.recruitment_task.service.JiraServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JiraSynchronizerImpl implements JiraSynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(JiraSynchronizerImpl.class);
    private final JiraService jiraService;

    @Autowired
    public JiraSynchronizerImpl(JiraServiceImpl jiraService) {
        this.jiraService = jiraService;
    }

    /**
     * Search for 5 tickets in one project, and move them
     * to the other project within same Jira instance.
     * When moving tickets, please move following fields:
     * - summary (title)
     * - description
     * - priority
     * Bonus points for syncing comments.
     */

    public void moveTasksToOtherProject() throws Exception {
        try {
            jiraService.moveTasksToOtherProject();
        } catch (Exception e) {
            logger.error("Error during task synchronization", e);
            throw e;
        }
    }
}
