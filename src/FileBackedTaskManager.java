import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {
                Task task = fromString(line);
                switch (task.getType()) {
                    case "EPIC":
                        manager.addEpic((Epic) task);
                        break;
                    case "SUBTASK":
                        manager.addSubtask((Subtask) task);
                        break;
                    default:
                        manager.addTask(task);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки задач из файла", e);
        }
        return manager;
    }

    private static String toString(Task task) {
        TaskType type;
        if (task.getClass() == Epic.class) {
            type = TaskType.EPIC;
        } else if (task.getClass() == Subtask.class) {
            type = TaskType.SUBTASK;
        } else {
            type = TaskType.TASK;
        }

        String epicId = "";
        if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            if (subtask.getEpic() != null) {
                epicId = String.valueOf(subtask.getEpic().getTaskId());
            } else {
                epicId = "0";
            }
        }

        String formattedDate = task.getStartTime() != null ? task.getStartTime().format(DATE_TIME_FORMATTER) : "null";
        long durationMinutes = task.getDuration() != null ? task.getDuration().toMinutes() : 0;

        return String.format("%d,%s,%s,%s,%s,%d,%s,%s",
                task.getTaskId(),
                type,
                task.getTaskName(),
                task.getStatus(),
                task.getDescription(),
                durationMinutes,
                formattedDate,
                epicId);
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        if (fields.length < 7) {
            throw new IllegalArgumentException("Недостаточно данных в строке: " + value);
        }

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        Duration duration = Duration.ofMinutes(Long.parseLong(fields[5]));
        LocalDateTime startTime = null;
        if (!"null".equals(fields[6])) {
            startTime = LocalDateTime.parse(fields[6], DATE_TIME_FORMATTER);
        }

        switch (type) {
            case TASK:
                return new Task(name, description, id, status, duration, startTime);
            case EPIC:
                return new Epic(name, description, id, status, new ArrayList<>(), duration, startTime);
            case SUBTASK:
                return new Subtask(name, description, id, status, duration, startTime);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи " + type);
        }
    }

    private void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("id,type,name,status,description,duration,startTime,epic\n");

            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }

            for (Task task : getAllEpic()) {
                if (task.getClass().equals(Epic.class)) {
                    Epic epic = (Epic) task;
                    writer.write(toString(epic) + "\n");
                }
            }

            for (Task task : getAllSubtasks()) {
                if (task.getClass().equals(Subtask.class)) {
                    Subtask subtask = (Subtask) task;
                    writer.write(toString(subtask) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении задачи в файл", e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public Task createTask(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        Task newTask = super.createTask(name, description, status, duration, startTime);
        save();
        return newTask;
    }

    @Override
    public Subtask createSubtask(String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        Subtask newSubtask = super.createSubtask(name, description, status, duration, startTime);
        save();
        return newSubtask;
    }

    @Override
    public Epic createEpic(String name, String description, Status status) {
        Epic newEpic = super.createEpic(name, description, status);
        save();
        return newEpic;
    }

    @Override
    public Epic createEpic(String name, String description, Status status, List<Subtask> subtasks, Duration duration, LocalDateTime startTime) {
        Epic newEpic = super.createEpic(name, description, status, subtasks, duration, startTime);
        save();
        return newEpic;
    }

    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(int taskId) {
        super.deleteEpicById(taskId);
        save();
    }

    @Override
    public void updateTask(Task task, int taskId) {
        super.updateTask(task, taskId);
        save();
    }

    @Override
    public void updateEpic(Epic epic, int taskId) {
        super.updateEpic(epic, taskId);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask, int taskId) {
        super.updateSubtask(subtask, taskId);
        save();
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        super.deleteSubtaskById(subtaskId);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void changeTaskStatus(Task task, int newStatus) {
        super.changeTaskStatus(task, newStatus);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }
}

