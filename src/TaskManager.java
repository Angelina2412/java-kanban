import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private List<Task> tasksList;

    public TaskManager() {
        tasksList = new ArrayList<>();
    }

    public void getAllTasks() {
        for (Task task : tasksList) {
            System.out.println("Task ID: " + task.getTaskId());
            System.out.println("Task Name: " + task.getTaskName());
            System.out.println("Task Description: " + task.getDescription());
            System.out.println("Task Status: " + task.getStatus());
            System.out.println();
        }
    }

    public void removeAllTasks() {
        tasksList.clear();
    }

    public Task getTaskById(int taskId) {
        for (Task task : tasksList) {
            if (task.getTaskId() == taskId) {
                System.out.println("Task ID: " + task.getTaskId());
                System.out.println("Task Name: " + task.getTaskName());
                System.out.println("Task Description: " + task.getDescription());
                System.out.println("Task Status: " + task.getStatus());
                return task;
            }
        }
        System.out.println("В Tracker отсутствует задача с taskId " + taskId);
        return null;
    }

    public void addTask(Task task) {
        tasksList.add(task);
    }

    public void removeTask(int taskId) {
        for (int i = 0; i < tasksList.size(); i++) {
            Task task = tasksList.get(i);
            if (task.getTaskId() == taskId) {
                tasksList.remove(i);
                System.out.println("Задача с taskId " + taskId + " успешно удалена.");
                return;
            }
        }

        System.out.println("В Tracker отсутствует задача с taskId " + taskId);
    }


    public void updateTask(int updateType, int taskId, String newValue) {
        for (Task task : tasksList) {
            if (task.getTaskId() == taskId) {
                switch (updateType) {
                    case 1:
                        task.setTaskName(newValue);
                        System.out.println("Название задачи успешно обновлено.");
                        break;
                    case 2:
                        task.setDescription(newValue);
                        System.out.println("Описание задачи успешно обновлено.");
                        break;
                    default:
                        System.out.println("Некорректное значение для обновления.");
                }
                return;
            }
        }

        System.out.println("Задача с taskId " + taskId + " не найдена в списке задач.");
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
        epic.updateStatus();
    }

}
