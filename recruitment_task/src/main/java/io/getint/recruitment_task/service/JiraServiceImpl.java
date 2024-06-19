package io.getint.recruitment_task.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.getint.recruitment_task.config.JiraConfig;
import io.getint.recruitment_task.model.Issue;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class JiraServiceImpl implements JiraService {
    private static final Logger logger = LoggerFactory.getLogger(JiraServiceImpl.class);
    private final JiraConfig jiraConfig;
    private final CloseableHttpClient httpClient;

    @Autowired
    public JiraServiceImpl(JiraConfig jiraConfig) {
        this.jiraConfig = jiraConfig;
        this.httpClient = HttpClients.createDefault();
    }

    public JiraServiceImpl(JiraConfig jiraConfig, CloseableHttpClient httpClient) {
        this.jiraConfig = jiraConfig;
        this.httpClient = httpClient;
    }

    private String getAuthHeader() {
        String auth = jiraConfig.getUsername() + ":" + jiraConfig.getApiToken();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        return "Basic " + new String(encodedAuth);
    }

    public List<Issue> getIssuesFromProject(String projectKey, int maxResults) throws IOException {
        String url = jiraConfig.getBaseUrl() + "/rest/api/3/search?jql=project=" + projectKey + "&maxResults=" + maxResults;
        logger.info("Fetching issues from URL: {}", url);
        HttpGet request = new HttpGet(url);
        request.addHeader("Authorization", getAuthHeader());
        request.addHeader("Accept", "application/json");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 401) {
                logger.error("Unauthorized: Check your username and API token.");
                throw new RuntimeException("Unauthorized: Check your username and API token.");
            }
            String result = EntityUtils.toString(response.getEntity());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(result);
            return mapper.convertValue(jsonNode.get("issues"), new TypeReference<>() {
            });
        } catch (IOException e) {
            logger.error("Error fetching issues from project: {}", projectKey, e);
            throw e;
        }
    }

    public void createIssueInProject(Issue issue, String targetProjectKey) throws IOException {
        String url = jiraConfig.getBaseUrl() + "/rest/api/3/issue";
        logger.info("Creating issue at URL: {}", url);
        HttpPost request = new HttpPost(url);
        request.addHeader("Authorization", getAuthHeader());
        request.addHeader("Content-Type", "application/json");

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode fields = mapper.createObjectNode();
        fields.putObject("project").put("key", targetProjectKey);
        fields.put("summary", issue.getFields().getSummary());
        fields.set("description", issue.getFields().getDescription());
        fields.putObject("priority").put("id", issue.getFields().getPriority().getId());
        fields.putObject("issuetype").put("name", "Task");

        ObjectNode payload = mapper.createObjectNode();
        payload.set("fields", fields);
        String json = mapper.writeValueAsString(payload);

        logger.info("Payload being sent: {}", json);

        request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseString = EntityUtils.toString(response.getEntity());
            logger.info("Response from JIRA: {}", responseString);
            if (response.getStatusLine().getStatusCode() != 201) {
                logger.error("Failed to create issue in project: {}. Response: {}. Full response: {}", targetProjectKey, response.getStatusLine(), responseString);
                throw new RuntimeException("Failed to create issue in project: " + targetProjectKey);
            }
            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            logger.error("Error creating issue in project: {}", targetProjectKey, e);
            if (e instanceof org.apache.http.client.ClientProtocolException) {
                logger.error("Client protocol error: {}", e.getMessage());
            } else {
                logger.error("IOException error: {}", e.getMessage());
            }
            throw e;
        }
    }

    public void moveTasksToOtherProject() throws IOException {
        try {
            List<Issue> issues = getIssuesFromProject(jiraConfig.getSourceProjectKey(), 5);
            for (Issue issue : issues) {
                createIssueInProject(issue, jiraConfig.getTargetProjectKey());
            }
        } catch (IOException e) {
            logger.error("Error moving tasks to other project", e);
            throw e;
        }
    }
}