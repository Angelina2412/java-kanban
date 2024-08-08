import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private Map<Integer, Task> tasksMap;
    private Map<Integer, Epic> epicsMap;
    private Map<Integer, Subtask> subtasksMap;
    private int taskIdCounter;

    private TreeSet<Task> prioritizedTasks;

    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasksMap = new HashMap<>();
        subtasksMap = new HashMap<>();
        epicsMap = new HashMap<>();
        taskIdCounter = 1;
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(
                Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
                          .thenComparing(Task::getTaskId)
        );
    }

    @Override
    public void addTask(Task task) {
        if (isOverlapping(task)) {
            System.out.println("В это время уже назначна другая задача");
            return;
        }
        task.setTaskId(taskIdCounter);
        tasksMap.put(taskIdCounter, task);
        prioritizedTasks.add(task);
        taskIdCounter++;
    }

    @Override
    public void addSubtask(Subtask subtask) {
        if (isOverlapping(subtask)) {
            System.out.println("В это время уже назначна другая подзадача");
            return;
        }
        subtask.setTaskId(taskIdCounter);
        subtasksMap.put(taskIdCounter, subtask);
        prioritizedTasks.add(subtask);
        taskIdCounter++;
    }

    @Override
    public void addEpic(Epic epic) {
        if (isOverlapping(epic)) {
            System.out.println("В это время уже назначена другая задача");
            return;
        }
        epic.setTaskId(taskIdCounter);
        epicsMap.put(taskIdCounter, epic);
        prioritizedTasks.add(epic);
        taskIdCounter++;
    }

    @Override
    public Task createTask(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        Task newTask = new Task(name, description, taskIdCounter, status, duration, startTime);
        addTask(newTask);
        return newTask;
    }

    @Override
    public Subtask createSubtask(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        Subtask newSubtask = new Subtask(name, description, taskIdCounter, status, duration, startTime);
        addSubtask(newSubtask);
        return newSubtask;
    }

    @Override
    public Epic createEpic(String name, String description, Status status) {
        Epic newEpic = new Epic(name, description, taskIdCounter, status);
        addEpic(newEpic);
        return newEpic;
    }

    @Override
    public Epic createEpic(String name, String description, Status status, List<Subtask> subtasks, Duration duration,
                           LocalDateTime startTime) {
        Epic newEpic = new Epic(name, description, taskIdCounter, status, subtasks, duration, startTime);
        addEpic(newEpic);
        updateEpicStatus(newEpic);
        return newEpic;
    }

    @Override
    public List<Task> getAllTasks() {
        return tasksMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<Task> getAllSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    @Override
    public List<Task> getAllEpic() {
        return epicsMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasksMap.get(taskId);
        if (task != null) {
            historyManager.addTask(task);
            return task;
        }

        System.out.println("Задача с ID" + taskId + " не найдена");
        return null;
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epicsMap.get(epicId);
        if (epic != null) {
            historyManager.addTask(epic);
            return epic;
        }

        System.out.println("Эпик с ID " + epicId + " не найден");
        return null;
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasksMap.get(subtaskId);
        if (subtask != null) {
            historyManager.addTask(subtask);
            return subtask;
        }

        System.out.println("Сабтаска с ID " + subtaskId + " не найдена");
        return null;
    }

    @Override
    public void deleteTaskById(int taskId) {
        Task task = tasksMap.remove(taskId);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(taskId);
            System.out.println("Задача с  ID " + taskId + " удалена");
        } else {
            System.out.println("Задача с ID " + taskId + " не найдена");
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
            System.out.println("Эпик с ID " + taskId + " удален с сабтасками");
        } else {
            System.out.println("Эпик с ID " + taskId + " не найден");
        }
    }

    @Override
    public void updateTask(Task task, int taskId) {
        if (tasksMap.containsKey(taskId)) {
            task.setTaskId(taskId);
            tasksMap.put(taskId, task);
            System.out.println("Задача с ID " + taskId + " была обновлена");
        } else {
            System.out.println("Задача с ID " + taskId + " не найдена");
        }
    }

    @Override
    public void updateEpic(Epic epic, int taskId) {
        if (epicsMap.containsKey(taskId)) {
            epic.setTaskId(taskId);
            epicsMap.put(taskId, epic);
            System.out.println("Эпик с  ID " + taskId + " был обновлен");
        } else {
            System.out.println("Эпик с ID " + taskId + " не найден");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask, int taskId) {
        if (subtasksMap.containsKey(taskId)) {
            subtask.setTaskId(taskId);
            subtasksMap.put(taskId, subtask);
            System.out.println("Сабтаска с ID " + taskId + " была обновлена");
            Epic epic = getEpicBySubtaskId(subtask);
            if (epic != null) {
                epic.getSubtasks().removeIf(existingSubtask -> existingSubtask.getTaskId() == taskId);
                epic.getSubtasks().add(subtask);
                updateEpicStatus(epic);
            }
        } else {
            System.out.println("Сабтаска с ID " + taskId + " не найдена");
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
            System.out.println("Сабтаска с ID " + subtaskId + " была удалена");
        } else {
            System.out.println("Сабтаска с ID " + subtaskId + " не найдена");
        }
    }

    @Override
    public void removeAllTasks() {
        tasksMap.clear();
        System.out.println("Все задачи удалены");
    }

    @Override
    public void removeAllEpics() {
        epicsMap.clear();
        System.out.println("Все эпики удалены");

    }

    @Override
    public void removeAllSubtasks() {
        subtasksMap.clear();
        System.out.println("Все сабтаски удалены");
    }

    @Override
    public List<Subtask> getSubtasksForEpic(Epic epic) {
        return epic.getSubtasks().stream().peek(subtask -> {
            System.out.println("ID: " + subtask.getTaskId() + " | Название: " + subtask.getTaskName() + " | Статус: "
                                       + subtask.getStatus());
        }).collect(Collectors.toList());
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
        if (epic.getSubtasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

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

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public void clear() {
        tasksMap.clear();
        subtasksMap.clear();
        epicsMap.clear();
        taskIdCounter = 1;
    }

//    public boolean isOverlapping(Task newTask) {
//        LocalDateTime newStart = newTask.getStartTime();
//        LocalDateTime newEnd = newTask.getEndTime();
//
//        if (newStart == null || newEnd == null) {
//            return false;
//        }
//
//        return prioritizedTasks.stream().anyMatch(existingTask -> {
//            LocalDateTime existingStart = existingTask.getStartTime();
//            LocalDateTime existingEnd = existingTask.getEndTime();
//            return existingStart != null && existingEnd != null &&
//                    (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart));
//        });
//    }

    public boolean isOverlapping(Task newTask) {
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        if (newStart == null || newEnd == null) {
            return false;
        }

        return prioritizedTasks.stream().anyMatch(existingTask -> {
            if (existingTask.getTaskId() == newTask.getTaskId()) {
                return false;
            }

            LocalDateTime existingStart = existingTask.getStartTime();
            LocalDateTime existingEnd = existingTask.getEndTime();
            return existingStart != null && existingEnd != null &&
                    (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart));
        });
    }

    public void clearPrioritizedTasks() {
        prioritizedTasks.clear();
    }
}
