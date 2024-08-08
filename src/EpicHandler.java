import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EpicHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(EpicHandler.class.getName());
    private TaskManager taskManager = Managers.getDefaultTaskManager();
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
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
            if (segments.length > 3 && "subtasks".equalsIgnoreCase(segments[3])) {
                handleGetSubtasksForEpic(exchange, taskId);
            } else {
                switch (method) {
                    case "GET":
                        if (taskId == -1) {
                            handleGetEpic(exchange);
                        } else {
                            handleGetEpicById(exchange, taskId);
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

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        String jsonResponse = gson.toJson(taskManager.getAllEpic());
        sendText(exchange, jsonResponse);
    }

    private void handleGetEpicById(HttpExchange exchange, int taskId) throws IOException {
        Task task = taskManager.getEpicById(taskId);
        if (task != null) {
            sendText(exchange, gson.toJson(task));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange, int taskId) throws IOException {
        try {
            InputStreamReader reader = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
            Epic epic = gson.fromJson(reader, Epic.class);

            if (taskId == -1) {
                Epic createdEpic = taskManager.createEpic(
                        epic.getTaskName(),
                        epic.getDescription(),
                        epic.getStatus(),
                        epic.getSubtasks(),
                        epic.getDuration(),
                        epic.getEndTime()
                );
                String responseBody = gson.toJson(createdEpic);
                exchange.sendResponseHeaders(201, -1);
                sendText(exchange, responseBody);
            } else {
                if (taskManager.getEpicById(taskId) != null) {
                    taskManager.updateEpic(epic, taskId);
                    Epic updatedEpic = taskManager.getEpicById(taskId);
                    String responseBody = gson.toJson(updatedEpic);
                    exchange.sendResponseHeaders(200, -1);
                    sendText(exchange, responseBody);
                } else {
                    sendNotFound(exchange);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка в  handlePost методе", e);
            sendInternalError(exchange, e);
        }
    }

    private void handleDelete(HttpExchange exchange, int taskId) throws IOException {
        try {
            Epic epic = taskManager.getEpicById(taskId);
            if (epic != null) {
                taskManager.deleteEpicById(taskId);
                sendText(exchange, "Эпик удален");
                exchange.sendResponseHeaders(200, -1);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка в  handleDelete методе", e);
            sendInternalError(exchange, e);
        }
    }

    private void handleGetSubtasksForEpic(HttpExchange exchange, int epicId) throws IOException {
        Epic epic = taskManager.getEpicById(epicId);
        if (epic != null) {
            List<Subtask> subtasks = taskManager.getSubtasksForEpic(epic);
            sendText(exchange, gson.toJson(subtasks));
        } else {
            sendNotFound(exchange);
        }
    }
}
