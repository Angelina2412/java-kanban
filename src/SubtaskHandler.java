import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private TaskManager taskManager = Managers.getDefaultTaskManager();
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] segments = path.split("/");
        int taskId = segments.length > 2 ? parseId(segments[2]) : -1;

        try {
            switch (method) {
                case "GET":
                    if (taskId == -1) {
                        handleGetSubtasks(exchange);
                    } else {
                        handleGetSubtaskById(exchange, taskId);
                    }
                    break;
                case "POST":
                    handlePost(exchange, taskId);
                    break;
                case "DELETE":
                    handleDelete(exchange, taskId);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange, e);
        }
    }

    private int parseId(String idStr) {
        try {
            return Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String jsonResponse = gson.toJson(taskManager.getAllSubtasks());
        sendText(exchange, jsonResponse);
    }

    private void handleGetSubtaskById(HttpExchange exchange, int taskId) throws IOException {
        Subtask subtask = taskManager.getSubtaskById(taskId);
        if (subtask != null) {
            sendText(exchange, gson.toJson(subtask));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange, int taskId) throws IOException {
        Subtask subtask = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), Subtask.class);

        if (taskId == -1) {
            Subtask createdSubtask = taskManager.createSubtask(subtask.getTaskName(), subtask.getDescription(), subtask.getStatus(),
                                                               subtask.getDuration(), subtask.getStartTime());
            String jsonResponse = gson.toJson(createdSubtask);
            exchange.sendResponseHeaders(201, -1);
            sendText(exchange, jsonResponse);
        } else {
            if (taskManager.getSubtaskById(taskId) != null) {
                taskManager.updateSubtask(subtask, taskId);
                Subtask updatedSubtask = taskManager.getSubtaskById(taskId);
                if (updatedSubtask != null) {
                    String jsonResponse = gson.toJson(updatedSubtask);
                    exchange.sendResponseHeaders(200, -1);
                    sendText(exchange, jsonResponse);
                } else {
                    sendNotFound(exchange);
                }
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handleDelete(HttpExchange exchange, int taskId) throws IOException {
        if (taskId != -1) {
            Subtask subtask = taskManager.getSubtaskById(taskId);
            if (subtask != null) {
                taskManager.deleteSubtaskById(taskId);
                sendText(exchange, "Подзадача удалена");
                exchange.sendResponseHeaders(200, -1);
            } else {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
