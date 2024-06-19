package io.getint.recruitment_task.synchronizer;

import io.getint.recruitment_task.service.JiraServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class JiraSynchronizerTests {

    private JiraServiceImpl jiraService;
    private JiraSynchronizerImpl jiraSynchronizer;

    @Before
    public void setUp() {
        jiraService = Mockito.mock(JiraServiceImpl.class);
        jiraSynchronizer = new JiraSynchronizerImpl(jiraService);
    }

    @Test
    public void shouldSyncTasks() throws Exception {
        doNothing().when(jiraService).moveTasksToOtherProject();

        jiraSynchronizer.moveTasksToOtherProject();

        verify(jiraService, times(1)).moveTasksToOtherProject();
    }
}
