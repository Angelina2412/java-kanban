import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicsTest {

    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();

        manager.removeAllSubtasks();
        manager.removeAllEpics();
        manager.removeAllTasks();
        manager.clearPrioritizedTasks();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        List<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Подзадача 1", "Описание для подзадачи 1", 13, Status.NEW, Duration.ofHours(1),
                                  LocalDateTime.now().plusHours(10)));
        subtasks1.add(new Subtask("Подзадача 2", "Описание для подзадачи 2", 14, Status.NEW, Duration.ofHours(2),
                                  LocalDateTime.now().plusHours(12)));

        Epic epic = manager.createEpic("Эпик", "Описание для эпика", Status.NEW, subtasks1, Duration.ZERO, null);
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении эпика");

        List<Task> epicsFromManager = manager.getAllEpic();
        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Эпик", epicsFromManager.get(0).getTaskName(), "Некорректное имя эпика");

    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        List<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Подзадача 1", "Описание для подзадачи 1", 13, Status.NEW, Duration.ofHours(1),
                                  LocalDateTime.now().plusHours(10)));
        subtasks1.add(new Subtask("Подзадача 2", "Описание для подзадачи 2", 14, Status.NEW, Duration.ofHours(2),
                                  LocalDateTime.now().plusHours(12)));

        Epic epic = manager.createEpic("Эпик", "Описание для эпика", Status.NEW, subtasks1, Duration.ZERO, null);
        int epicId = epic.getTaskId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код ответа при получении эпика");

        Epic fetchedEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(fetchedEpic, "Эпик не возвращается");
        assertEquals("Эпик", fetchedEpic.getTaskName(), "Некорректное имя эпика");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        List<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Подзадача 1", "Описание для подзадачи 1", 13, Status.NEW, Duration.ofHours(1),
                                  LocalDateTime.now().plusHours(10)));
        subtasks1.add(new Subtask("Подзадача 2", "Описание для подзадачи 2", 14, Status.NEW, Duration.ofHours(2),
                                  LocalDateTime.now().plusHours(12)));

        Epic epic = manager.createEpic("Эпик", "Описание для эпика", Status.NEW, subtasks1, Duration.ZERO, null);
        int epicId = epic.getTaskId();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epicId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код ответа при удалении эпика");

        HttpRequest getRequest = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, getResponse.statusCode(), "Эпик не удален");
    }

    @Test
    public void testGetNonExistentEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный код ответа при запросе несуществующего эпика");
    }

}

