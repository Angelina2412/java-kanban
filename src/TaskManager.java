import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {

    private Map<Integer, Task> tasksMap;
    private Map<Integer, Subtask> subtasksMap;
    private Map<Integer, Epic> epicsMap;
    private int taskIdCounter;

    public TaskManager() {
        tasksMap = new HashMap<>();
        subtasksMap = new HashMap<>();
        epicsMap = new HashMap<>();
        taskIdCounter = 1;
    }

    public void addTask(Task task) {
        task.setTaskId(taskIdCounter);
        tasksMap.put(taskIdCounter, task);
        taskIdCounter++;
    }

    public void addSubtask(Subtask subtask) {
        subtask.setTaskId(taskIdCounter);
        subtasksMap.put(taskIdCounter, subtask);
        taskIdCounter++;
    }

    public void addEpic(Epic epic) {
        epic.setTaskId(taskIdCounter);
        epicsMap.put(taskIdCounter, epic);
        taskIdCounter++;
    }

    public Task createTask(String name, String description, Status status) {
        Task newTask = new Task(name, description, taskIdCounter, status);
        addTask(newTask);
        return newTask;
    }

    public Subtask createSubtask(String name, String description, Status status) {
        Subtask newSubtask = new Subtask(name, description, taskIdCounter, status);
        addSubtask(newSubtask);
        return newSubtask;
    }

    public Epic createEpic(String name, String description, Status status) {
        Epic newEpic = new Epic(name, description, taskIdCounter, status, new ArrayList<>());
        addEpic(newEpic);
        return newEpic;
    }

    public Epic createEpic(String name, String description, Status status, List<Subtask> subtasks) {
        Epic newEpic = new Epic(name, description, taskIdCounter, status, subtasks);
        addEpic(newEpic);
        updateEpicStatus(newEpic);
        return newEpic;
    }


    public void getAllTasks() {
        for (Task task : tasksMap.values()) {
            System.out.println("Task ID: " + task.getTaskId());
            System.out.println("Task Name: " + task.getTaskName());
            System.out.println("Task Description: " + task.getDescription());
            System.out.println("Task Status: " + task.getStatus());
            System.out.println();
        }
    }

    public void getAllSubtasks() {
        for (Subtask subtask : subtasksMap.values()) {
            System.out.println("Task ID: " + subtask.getTaskId());
            System.out.println("Task Name: " + subtask.getTaskName());
            System.out.println("Task Description: " + subtask.getDescription());
            System.out.println("Task Status: " + subtask.getStatus());
            System.out.println();
        }
    }

    public void getAllEpic() {
        for (Epic epic : epicsMap.values()) {
            System.out.println("Task ID: " + epic.getTaskId());
            System.out.println("Task Name: " + epic.getTaskName());
            System.out.println("Task Description: " + epic.getDescription());
            System.out.println("Task Status: " + epic.getStatus());
            System.out.println();
        }
    }

    public Task getTaskById(int taskId) {
        Task task = tasksMap.get(taskId);
        if (task != null) {
            return task;
        }

        System.out.println("Task with ID " + taskId + " not found");
        return null;
    }

    public Epic getEpicById(int epicId) {
        Epic epic = epicsMap.get(epicId);
        if (epic != null) {
            return epic;
        }

        System.out.println("Epic with ID " + epicId + " not found");
        return null;
    }

    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasksMap.get(subtaskId);
        if (subtask != null) {
            return subtask;
        }

        System.out.println("Subtask with ID " + subtaskId + " not found");
        return null;
    }

    public boolean deleteTaskById(int taskId) {
        if (tasksMap.containsKey(taskId)) {
            tasksMap.remove(taskId);
            System.out.println("Subtask with ID " + taskId + " has been deleted");
            return true;
        } else {
            System.out.println("Subtask with ID " + taskId + " not found");
            return false;
        }
    }

    public boolean deleteEpicById(int taskId) {
        if (epicsMap.containsKey(taskId)) {
            epicsMap.remove(taskId);
            System.out.println("Epic with ID " + taskId + " has been deleted");
            return true;
        } else {
            System.out.println("Epic with ID " + taskId + " not found");
            return false;
        }

    }

    public boolean deleteSubtaskById(int taskIdId) {
        if (subtasksMap.containsKey(taskIdId)) {
            subtasksMap.remove(taskIdId);
            System.out.println("Task with ID " + taskIdId + " has been deleted");
            return true;
        } else {
            System.out.println("Task with ID " + taskIdId + " not found");
            return false;
        }
    }

    public boolean updateTask(Task task, int taskId) {
        if (tasksMap.containsKey(taskId)) {
            Task existingTask = epicsMap.get(taskId);
            task.setTaskId(existingTask.getTaskId());
            tasksMap.put(taskId, task);
            System.out.println("Task with ID " + taskId + " has been updated");
            return true;
        } else {
            System.out.println("Task with ID " + taskId + " not found");
            return false;
        }
    }

    public boolean updateEpic(Epic epic, int epicId) {
        if (epicsMap.containsKey(epicId)) {
            Epic existingEpic = epicsMap.get(epicId);
            epic.setTaskId(existingEpic.getTaskId());
            epicsMap.put(epicId, epic);
            System.out.println("Epic with ID " + epicId + " has been updated");
            return true;
        } else {
            System.out.println("Epic with ID " + epicId + " not found");
            return false;
        }
    }

    public boolean updateSubtask(Subtask subtask, int subtaskId) {
        if (subtasksMap.containsKey(subtaskId)) {
            Subtask existingSubtask = subtasksMap.get(subtaskId);
            subtask.setTaskId(existingSubtask.getTaskId());
            subtasksMap.put(subtaskId, subtask);
            System.out.println("Subtask with ID " + subtaskId + " has been updated");
            return true;
        } else {
            System.out.println("Subtask with ID " + subtaskId + " not found");
            return false;
        }
    }

    public void removeAllTasks() {
        tasksMap.clear();
        System.out.println("Tasks was deleted");
    }

    public void removeAllEpics() {
        epicsMap.clear();
        System.out.println("Epics was deleted");

    }

    public void removeAllSubtasks() {
        subtasksMap.clear();
        System.out.println("Subtasks was deleted");
    }


    public List<Subtask> getSubtasksForEpic(Epic epic) {
        List<Subtask> subtasks = epic.getSubtasks();
        for (Subtask subtask : subtasks) {
            System.out.println("ID: " + subtask.getTaskId() + " | Название: " + subtask.getTaskName() + " | Статус: "
                    + subtask.getStatus());
        }

        return subtasks;
    }


    public void changeTaskStatus(Task task, int newStatus) {
        switch (newStatus) {
            case 1:
                task.setStatus(Status.NEW);
                System.out.println("Статус задачи успешно изменен на NEW");
                break;
            case 2:
                task.setStatus(Status.IN_PROGRESS);
                System.out.println("Статус задачи успешно изменен на IN_PROGRESS");
                break;
            case 3:
                task.setStatus(Status.DONE);
                System.out.println("Статус задачи успешно изменен на DONE");
                break;
            default:
                System.out.println("Некорректный выбор статуса. Пожалуйста, выберите от 1 до 3.");
        }
    }

    public void changeEpicStatus(Epic epic) {
        updateEpicStatus(epic);
    }

    public void updateEpicStatus(Epic epic) {
        boolean allSubtasksDone = true;
        boolean allSubtasksNew = true;

        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() != Status.DONE) {
                allSubtasksDone = false;
            }
            if (subtask.getStatus() != Status.NEW) {
                allSubtasksNew = false;
            }
        }

        if (allSubtasksDone) {
            epic.setStatus(Status.DONE);
        } else if (allSubtasksNew) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }
}
