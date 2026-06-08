package lv.smiltenesnkup.dvs;

import lv.smiltenesnkup.dvs.sharepoint.service.SharePointGraphService;
import lv.smiltenesnkup.dvs.task.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Pārbauda Uzdevumu servisa biznesa loģiku.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private SharePointGraphService graphService;

    @InjectMocks
    private TaskServiceImpl taskService;

    /**
     * Pārbauda, vai serviss veiksmīgi saņem un atgriež lietotāju sarakstu no Graph API.
     */
    @Test
    void shouldReturnUsersFromGraphApi() {
        // Arrange
        String query = "Jānis";
        List<String> mockUsers = List.of("Jānis Liniņš");

        when(graphService.searchUsers(query)).thenReturn(mockUsers);

        // Act
        List<String> result = taskService.searchUsers(query);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Jānis Liniņš", result.get(0));

        // Pārliecinās, ka Graph API serviss tika izsaukts tieši vienu reizi
        verify(graphService, times(1)).searchUsers(query);
    }
}