package io.getint.recruitment_task.synchronizer;

public interface JiraSynchronizer {

    /**
     * Search for 5 tickets in one project, and move them
     * to the other project within same Jira instance.
     * When moving tickets, please move following fields:
     * - summary (title)
     * - description
     * - priority
     * Bonus points for syncing comments.
     */
    void moveTasksToOtherProject() throws Exception;
}
