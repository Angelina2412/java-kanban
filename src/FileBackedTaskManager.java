import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
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
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getTaskId(),
                type,
                task.getTaskName(),
                task.getStatus(),
                task.getDescription(),
                epicId);

    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                return new Task(name, description, id, status);
            case EPIC:
                return new Epic(name, description, id, status, new ArrayList<>());
            case SUBTASK:
                return new Subtask(name, description, id, status);
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи " + type);
        }
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

    public void save() {
        BufferedWriter writer = null;
        try {
            writer = Files.newBufferedWriter(file.toPath());
            writer.write("id,type,name,status,description,epic\n");

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
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
    public void addTask(Task task, int taskId) {
        super.addTask(task, taskId);
        save();
    }

    @Override
    public void addSubtask(Subtask subtask, int taskId) {
        super.addSubtask(subtask, taskId);
        save();
    }

    @Override
    public void addEpic(Epic epic, int taskId) {
        super.addEpic(epic, taskId);
        save();
    }

    @Override
    public Task createTask(String name, String description, Status status) {
        Task newTask = super.createTask(name, description, status);
        save();
        return newTask;
    }

    @Override
    public Subtask createSubtask(String name, String description, Status status) {
        Subtask newSubtask = super.createSubtask(name, description, status);
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
    public Epic createEpic(String name, String description, Status status, List<Subtask> subtasks) {
        Epic newEpic = super.createEpic(name, description, status, subtasks);
        save();
        return newEpic;
    }

    @Override
    public Task createTask(String name, String description, Status status, int taskId) {
        Task newTask = super.createTask(name, description, status, taskId);
        save();
        return newTask;
    }

    @Override
    public Subtask createSubtask(String name, String description, Status status, int taskId) {
        Subtask newSubtask = super.createSubtask(name, description, status, taskId);
        save();
        return newSubtask;
    }

    @Override
    public Epic createEpic(String name, String description, Status status, int taskId) {
        Epic newEpic = super.createEpic(name, description, status, taskId);
        save();
        return newEpic;
    }

    @Override
    public Epic createEpic(String name, String description, Status status, int taskId, List<Subtask> subtasks) {
        Epic newEpic = super.createEpic(name, description, status, taskId, subtasks);
        save();
        return newEpic;
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Task> getAllSubtasks() {
        return super.getAllSubtasks();
    }


    @Override
    public List<Task> getAllEpic() {
        return super.getAllEpic();
    }

    @Override
    public Task getTaskById(int taskId) {
        return super.getTaskById(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        return super.getEpicById(epicId);
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        return super.getSubtaskById(subtaskId);
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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
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
    public List<Subtask> getSubtasksForEpic(Epic epic) {
        return super.getSubtasksForEpic(epic);
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

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}

