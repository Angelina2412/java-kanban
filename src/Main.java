import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Поехали!");
        Task taskFirst = taskManager.createTask("Meeting with friend", "Sasha", Status.NEW);
        taskManager.createTask("Walk with dog", "Evening", Status.IN_PROGRESS);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(taskManager.createSubtask("Reading", "Book", Status.DONE));
        subtasks.add(taskManager.createSubtask("Swimming", "ZIL", Status.DONE));
        Epic epicFirst = taskManager.createEpic("Weekend", "A lot of tasks", Status.NEW, subtasks);
        taskManager.createEpic("Week", "Work", Status.NEW);
        taskManager.createSubtask("Shopping", "Afimoll", Status.IN_PROGRESS);
        taskManager.createSubtask("Cooking", "Dinner", Status.IN_PROGRESS);
        taskManager.getAllSubtasks();
        Epic epicSecond = taskManager.createEpic("Sunday", "day off", Status.IN_PROGRESS);
        taskManager.getAllEpic();
        Subtask newSubtask = taskManager.createSubtask("Cooking", "Dinner", Status.NEW);
        taskManager.updateSubtask(newSubtask, 2);
        taskManager.getAllSubtasks();
        System.out.println("Выберите новый статус для задачи: 1 - NEW, 2 - IN_PROGRESS, 3 - DONE");
        int numberForNewStatus = scanner.nextInt();
        taskManager.changeTaskStatus(newSubtask, numberForNewStatus);
        taskManager.getSubtaskById(2);
        taskManager.deleteTaskById(1);
        taskManager.getAllTasks();
        taskManager.updateTask(taskManager.createTask("Walk with dogs", "Morning", Status.IN_PROGRESS), 2);
        taskManager.getAllTasks();
        taskManager.getSubtasksForEpic(epicFirst);
        taskManager.removeAllSubtasks();
        taskManager.getAllSubtasks();
    }
}
