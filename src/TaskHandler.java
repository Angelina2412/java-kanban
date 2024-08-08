import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskHandler extends BaseHttpHandler {

    private static final Logger logger = Logger.getLogger(TaskHandler.class.getName());
    private TaskManager taskManager = Managers.getDefaultTaskManager();
    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
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
                        handleGetTasks(exchange);
                    } else {
                        handleGetTaskById(exchange, taskId);
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
            logger.log(Level.SEVERE, "Такого метода не существует", e);
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

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String jsonResponse = gson.toJson(taskManager.getAllTasks());
        sendText(exchange, jsonResponse);
    }

    private void handleGetTaskById(HttpExchange exchange, int taskId) throws IOException {
        Task task = taskManager.getTaskById(taskId);
        if (task != null) {
            sendText(exchange, gson.toJson(task));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange, int taskId) throws IOException {
        Task task = gson.fromJson(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8), Task.class);
        System.out.println("Received task: " + task);
        if (taskId == -1) {
            if (taskManager.isOverlapping(task)) {
                System.out.println("Task overlaps, sending 406");
                exchange.sendResponseHeaders(406, -1);
                sendText(exchange, "Задача пересекается с уже существующей");
            } else {
                Task createdTask = taskManager.createTask(task.getTaskName(), task.getDescription(), task.getStatus(),
                                                          task.getDuration(), task.getStartTime());
                String jsonResponse = gson.toJson(createdTask);
                exchange.sendResponseHeaders(201, -1);
                sendText(exchange, jsonResponse);
            }
        } else {
            taskManager.updateTask(task, taskId);
            Task updatedTask = taskManager.getTaskById(taskId);
            if (updatedTask != null) {
                String jsonResponse = gson.toJson(updatedTask);
                exchange.sendResponseHeaders(200, -1);
                sendText(exchange, jsonResponse);
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handleDelete(HttpExchange exchange, int taskId) throws IOException {
        Task task = taskManager.getTaskById(taskId);
        if (task != null) {
            taskManager.deleteTaskById(taskId);
            sendText(exchange, "Задача удалена");
        } else {
            sendNotFound(exchange);
        }
    }
}
