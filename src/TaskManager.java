import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    void addTask(Task task, int taskId);

    void addSubtask(Subtask subtask, int taskId);

    void addEpic(Epic epic, int taskId);

    Task createTask(String name, String description, Status status, Duration duration, LocalDateTime startTime);

    Subtask createSubtask(String name, String description, Status status, Duration duration, LocalDateTime startTime);

    Epic createEpic(String name, String description, Status status, Duration duration, LocalDateTime startTime);

    Epic createEpic(String name, String description, int taskId, Status status, Duration duration, LocalDateTime startTime);

    Epic createEpic(String name, String description, int taskId, Status status, List<Subtask> subtasks, Duration duration, LocalDateTime startTime);

    Epic createEpic(String name, String description, Status status, List<Subtask> subtasks, Duration duration, LocalDateTime startTime);


    Task createTask(String name, String description, Status status, int taskId, Duration duration, LocalDateTime startTime);

    Subtask createSubtask(String name, String description, Status status, int taskId, Duration duration, LocalDateTime startTime);

    List<Task> getAllTasks();

    List<Task> getAllSubtasks();

    List<Task> getAllEpic();

    Task getTaskById(int taskId);

    Epic getEpicById(int epicId);

    Subtask getSubtaskById(int subtaskId);

    void deleteTaskById(int taskId);

    void deleteEpicById(int taskId);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int subtaskId);

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtasks();

    List<Subtask> getSubtasksForEpic(Epic epic);

    void changeTaskStatus(Task task, int newStatus);

    private void changeEpicStatus(Epic epic) {
        updateEpicStatus(epic);
    }

    ;

    void updateEpicStatus(Epic epic);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    void clear();
}
