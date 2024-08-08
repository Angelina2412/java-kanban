import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = manager.createTask("Прогулка", "С собакой", Status.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Response Code: " + response.statusCode()); // Печать кода ответа
        System.out.println("Response Body: " + response.body()); // Печать тела ответа

        assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении задачи");

        List<Task> tasksFromManager = manager.getAllTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Прогулка", tasksFromManager.get(0).getTaskName(), "Некорректное имя задачи");
    }


    @Test
    public void testAddExistingTask() throws IOException, InterruptedException {
        manager.createTask("Прогулка", "С собакой", Status.NEW,
                                       Duration.ofHours(1), LocalDateTime.now().plusHours(0));
        Task taskSecond = manager.createTask("Прогулка", "С собакой", Status.NEW,
                                       Duration.ofHours(1), LocalDateTime.now().plusHours(0));
        String taskSecondJson = gson.toJson(taskSecond);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest requestSecond = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskSecondJson)).build();

        HttpResponse<String> response = client.send(requestSecond, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Неверный код ответа при добавлении задачи");
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task = manager.createTask("Прогулка", "С собакой", Status.NEW,
                                       Duration.ofHours(1), LocalDateTime.now().plusHours(0));
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код ответа при получении задач");

        Type listType = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(response.body(), listType);

        assertNotNull(tasks, "Задачи не возвращаются");
        assertEquals(2, tasks.size(), "Некорректное количество задач");
        assertEquals("Прогулка", tasks.get(0).getTaskName(), "Некорректное имя задачи");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = manager.createTask("Прогулка", "С собакой", Status.NEW,
                                       Duration.ofHours(1), LocalDateTime.now().plusHours(0));
        int taskId = task.getTaskId();
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + taskId);
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Неверный код ответа при удалении задачи");
        assertNull(manager.getTaskById(taskId), "Задача не удалена");
    }

    @Test
    public void testGetNonExistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Неверный код ответа при запросе несуществующей задачи");
    }
}
