import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addSubtask(Subtask subtask);

    void addEpic(Epic epic);

    void addTask(Task task, int taskId);

    void addSubtask(Subtask subtask, int taskId);

    void addEpic(Epic epic, int taskId);

    Task createTask(String name, String description, Status status);

    Subtask createSubtask(String name, String description, Status status);

    Epic createEpic(String name, String description, Status status);

    Epic createEpic(String name, String description, Status status, List<Subtask> subtasks);

    Task createTask(String name, String description, Status status, int taskId);

    Subtask createSubtask(String name, String description, Status status, int taskId);

    Epic createEpic(String name, String description, Status status, int taskId);

    Epic createEpic(String name, String description, Status status, int taskId, List<Subtask> subtasks);

    void getAllTasks();

    void getAllSubtasks();

    void getAllEpic();

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

    void changeEpicStatus(Epic epic);

    void updateEpicStatus(Epic epic);
}
