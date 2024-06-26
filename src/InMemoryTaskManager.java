import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private Map<Integer, Task> tasksMap;
    private Map<Integer, Subtask> subtasksMap;
    private Map<Integer, Epic> epicsMap;
    private int taskIdCounter;

    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasksMap = new HashMap<>();
        subtasksMap = new HashMap<>();
        epicsMap = new HashMap<>();
        taskIdCounter = 1;
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) {
        task.setTaskId(taskIdCounter);
        tasksMap.put(taskIdCounter, task);
        taskIdCounter++;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setTaskId(taskIdCounter);
        subtasksMap.put(taskIdCounter, subtask);
        taskIdCounter++;
    }

    @Override
    public void addEpic(Epic epic) {
        epic.setTaskId(taskIdCounter);
        epicsMap.put(taskIdCounter, epic);
        taskIdCounter++;
    }

    @Override
    public void addTask(Task task, int taskId) {
        task.setTaskId(taskId);
        tasksMap.put(taskId, task);
    }

    @Override
    public void addSubtask(Subtask subtask, int taskId) {
        subtask.setTaskId(taskId);
        subtasksMap.put(taskIdCounter, subtask);
    }

    @Override
    public void addEpic(Epic epic, int taskId) {
        epic.setTaskId(taskId);
        epicsMap.put(taskIdCounter, epic);
    }

    @Override
    public Task createTask(String name, String description, Status status) {
        Task newTask = new Task(name, description, taskIdCounter, status);
        addTask(newTask);
        return newTask;
    }

    @Override
    public Subtask createSubtask(String name, String description, Status status) {
        Subtask newSubtask = new Subtask(name, description, taskIdCounter, status);
        addSubtask(newSubtask);
        return newSubtask;
    }

    @Override
    public Epic createEpic(String name, String description, Status status) {
        Epic newEpic = new Epic(name, description, taskIdCounter, status, new ArrayList<>());
        addEpic(newEpic);
        return newEpic;
    }

    @Override
    public Epic createEpic(String name, String description, Status status, List<Subtask> subtasks) {
        Epic newEpic = new Epic(name, description, taskIdCounter, status, subtasks);
        addEpic(newEpic);
        updateEpicStatus(newEpic);
        return newEpic;
    }

    @Override
    public Task createTask(String name, String description, Status status, int taskId) {
        Task newTask = new Task(name, description, taskId, status);
        addTask(newTask, taskId);
        return newTask;
    }

    @Override
    public Subtask createSubtask(String name, String description, Status status, int taskId) {
        Subtask newSubtask = new Subtask(name, description, taskId, status);
        addSubtask(newSubtask, taskId);
        return newSubtask;
    }

    @Override
    public Epic createEpic(String name, String description, Status status, int taskId) {
        Epic newEpic = new Epic(name, description, taskId, status, new ArrayList<>());
        addEpic(newEpic, taskId);
        return newEpic;
    }

    @Override
    public Epic createEpic(String name, String description, Status status, int taskId, List<Subtask> subtasks) {
        Epic newEpic = new Epic(name, description, taskId, status, subtasks);
        addEpic(newEpic);
        updateEpicStatus(newEpic);
        return newEpic;
    }

    @Override
    public List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(tasksMap.values());
        return allTasks;
    }

    @Override
    public void getAllSubtasks() {
        for (Subtask subtask : subtasksMap.values()) {
            System.out.println("Task ID: " + subtask.getTaskId());
            System.out.println("Task Name: " + subtask.getTaskName());
            System.out.println("Task Description: " + subtask.getDescription());
            System.out.println("Task Status: " + subtask.getStatus());
            System.out.println();
        }
    }

    @Override
    public void getAllEpic() {
        for (Epic epic : epicsMap.values()) {
            System.out.println("Task ID: " + epic.getTaskId());
            System.out.println("Task Name: " + epic.getTaskName());
            System.out.println("Task Description: " + epic.getDescription());
            System.out.println("Task Status: " + epic.getStatus());
            System.out.println();
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasksMap.get(taskId);
        if (task != null) {
            historyManager.addTask(task);
            return task;
        }

        System.out.println("Task with ID " + taskId + " not found");
        return null;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epicsMap.get(epicId);
        if (epic != null) {
            historyManager.addTask(epic);
            return epic;
        }

        System.out.println("Epic with ID " + epicId + " not found");
        return null;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasksMap.get(subtaskId);
        if (subtask != null) {
            historyManager.addTask(subtask);
            return subtask;
        }

        System.out.println("Subtask with ID " + subtaskId + " not found");
        return null;
    }

    @Override
    public void deleteTaskById(int taskId) {
        if (tasksMap.containsKey(taskId)) {
            tasksMap.remove(taskId);
            System.out.println("Subtask with ID " + taskId + " has been deleted");
        } else {
            System.out.println("Subtask with ID " + taskId + " not found");
        }
    }

    @Override
    public void deleteEpicById(int taskId) {
        if (epicsMap.containsKey(taskId)) {
            Epic epic = epicsMap.get(taskId);
            for (Subtask subtask : epic.getSubtasks()) {
                subtasksMap.remove(subtask.getTaskId());
            }
            epicsMap.remove(taskId);
            System.out.println("Epic with ID " + taskId + " has been deleted along with its subtasks");
        } else {
            System.out.println("Epic with ID " + taskId + " not found");
        }
    }

    @Override
    public void updateTask(Task task) {
        int taskId = task.getTaskId();
        if (tasksMap.containsKey(taskId)) {
            tasksMap.put(taskId, task);
            System.out.println("Task with ID " + taskId + " has been updated");
        } else {
            System.out.println("Task with ID " + taskId + " not found");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getTaskId();
        if (epicsMap.containsKey(epicId)) {
            epicsMap.put(epicId, epic);
            System.out.println("Epic with ID " + epicId + " has been updated");
        } else {
            System.out.println("Epic with ID " + epicId + " not found");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getTaskId();
        if (subtasksMap.containsKey(subtaskId)) {
            subtasksMap.put(subtaskId, subtask);
            System.out.println("Subtask with ID " + subtaskId + " has been updated");
            Epic epic = getEpicBySubtaskId(subtask);
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                epic.getSubtasks().add(subtask);
                updateEpicStatus(epic);
            }
        } else {
            System.out.println("Subtask with ID " + subtaskId + " not found");
        }
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        if (subtasksMap.containsKey(subtaskId)) {
            Subtask subtask = subtasksMap.get(subtaskId);
            subtasksMap.remove(subtaskId);
            Epic epic = getEpicBySubtaskId(subtask);
            if (epic != null) {
                epic.getSubtasks().remove(subtask);
                updateEpicStatus(epic);
            }
            System.out.println("Subtask with ID " + subtaskId + " has been deleted");
        } else {
            System.out.println("Subtask with ID " + subtaskId + " not found");
        }
    }


    @Override
    public void removeAllTasks() {
        tasksMap.clear();
        System.out.println("Tasks was deleted");
    }

    @Override
    public void removeAllEpics() {
        epicsMap.clear();
        System.out.println("Epics was deleted");

    }

    @Override
    public void removeAllSubtasks() {
        subtasksMap.clear();
        System.out.println("Subtasks was deleted");
    }


    @Override
    public List<Subtask> getSubtasksForEpic(Epic epic) {
        List<Subtask> subtasks = epic.getSubtasks();
        for (Subtask subtask : subtasks) {
            System.out.println("ID: " + subtask.getTaskId() + " | Название: " + subtask.getTaskName() + " | Статус: "
                    + subtask.getStatus());
        }
        return subtasks;
    }

    @Override
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

    @Override
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

    private Epic getEpicBySubtaskId(Subtask subtask) {
        for (Epic epic : epicsMap.values()) {
            if (epic.getSubtasks().contains(subtask)) {
                return epic;
            }
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
