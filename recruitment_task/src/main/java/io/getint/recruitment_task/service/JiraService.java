package io.getint.recruitment_task.service;

import io.getint.recruitment_task.model.Issue;

import java.io.IOException;
import java.util.List;

public interface JiraService {
    List<Issue> getIssuesFromProject(String projectKey, int maxResults) throws IOException;
    void createIssueInProject(Issue issue, String targetProjectKey) throws IOException;
    void moveTasksToOtherProject() throws IOException;
}