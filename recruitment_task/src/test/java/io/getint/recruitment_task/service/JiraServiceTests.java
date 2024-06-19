package io.getint.recruitment_task.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.getint.recruitment_task.config.JiraConfig;
import io.getint.recruitment_task.model.Issue;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;


public class JiraServiceTests {

    private JiraServiceImpl jiraService;
    private JiraConfig jiraConfig;
    private CloseableHttpClient httpClient;
    private CloseableHttpResponse response;
    private StatusLine statusLine;
    private HttpEntity entity;

    @Before
    public void setUp() {
        jiraConfig = Mockito.mock(JiraConfig.class);
        httpClient = Mockito.mock(CloseableHttpClient.class);
        response = Mockito.mock(CloseableHttpResponse.class);
        statusLine = Mockito.mock(StatusLine.class);
        entity = Mockito.mock(HttpEntity.class);
        jiraService = new JiraServiceImpl(jiraConfig, httpClient);
    }

    @Test
    public void shouldFetchIssuesFromProject() throws Exception {
        String jsonResponse = "{ \"issues\": [{ \"fields\": { \"summary\": \"Test issue\", \"description\": { \"type\": \"doc\", \"content\": [{ \"type\": \"text\", \"text\": \"Test description\" }]}, \"priority\": { \"id\": \"1\" }}}]}";

        when(jiraConfig.getBaseUrl()).thenReturn("http://jira.example.com");
        when(jiraConfig.getUsername()).thenReturn("username");
        when(jiraConfig.getApiToken()).thenReturn("apiToken");
        when(statusLine.getStatusCode()).thenReturn(200);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(new StringEntity(jsonResponse));
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(response);

        List<Issue> issues = jiraService.getIssuesFromProject("TEST", 5);

        assertEquals(1, issues.size());
        assertEquals("Test issue", issues.get(0).getFields().getSummary());
        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
    }

    @Test
    public void shouldCreateIssueInProject() throws Exception {
        Issue issue = new Issue();
        Issue.Fields fields = new Issue.Fields();
        fields.setSummary("New issue");
        fields.setDescription(new ObjectMapper().createObjectNode().put("type", "doc").put("content", "New description"));
        Issue.Fields.Priority priority = new Issue.Fields.Priority();
        priority.setId("1");
        fields.setPriority(priority);
        issue.setFields(fields);

        when(jiraConfig.getBaseUrl()).thenReturn("http://jira.example.com");
        when(jiraConfig.getUsername()).thenReturn("username");
        when(jiraConfig.getApiToken()).thenReturn("apiToken");
        when(statusLine.getStatusCode()).thenReturn(201);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(response.getEntity()).thenReturn(new StringEntity("{\"id\":\"10000\"}"));
        when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(response);

        jiraService.createIssueInProject(issue, "TARGET");

        verify(httpClient, times(1)).execute(any(HttpUriRequest.class));
    }
}