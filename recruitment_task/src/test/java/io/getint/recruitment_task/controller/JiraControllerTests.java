package io.getint.recruitment_task.controller;

import io.getint.recruitment_task.config.TestSecurityConfig;
import io.getint.recruitment_task.synchronizer.JiraSynchronizerImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(JiraController.class)
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
public class JiraControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JiraSynchronizerImpl jiraSynchronizer;

    @Test
    public void shouldSyncIssues() throws Exception {
        doNothing().when(jiraSynchronizer).moveTasksToOtherProject();

        mockMvc.perform(get("/api/jira/sync"))
                .andExpect(status().isOk())
                .andExpect(content().string("Issues synchronized successfully"));

        verify(jiraSynchronizer, times(1)).moveTasksToOtherProject();
    }

    @Test
    public void shouldHandleSyncIssuesError() throws Exception {
        doThrow(new RuntimeException("Sync error")).when(jiraSynchronizer).moveTasksToOtherProject();

        mockMvc.perform(get("/api/jira/sync"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error synchronizing issues: Sync error - null"));


        verify(jiraSynchronizer, times(1)).moveTasksToOtherProject();
    }
}