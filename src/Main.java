import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Поехали!");
        Task firstTask = new Task("Поучиться", "Курс по Java", 123, Status.NEW);
        Task secondTask = new Task("Погулять с собакой", "Утром, днем и вечером", 12,
                Status.IN_PROGRESS);
        Task thirdTask = new Task("Сходить в бассейн", "Акватория Зил", 120, Status.DONE);
        Task lastTask = new Task("Позаниматься английским", "Italki", 199,
                Status.IN_PROGRESS);
        Subtask firstSubtaskForMoving = new Subtask("Упаковать вещи", "Все вещи", 66,
                Status.IN_PROGRESS);
        Subtask secondSubtaskForMoving = new Subtask("Вызвать такси", "Yandex Go", 67,
                Status.NEW);
        Subtask subtaskForTravel = new Subtask("Купить билеты на самолет", "Aviasales", 68,
                Status.NEW);
        List<Subtask> subtasksForMoving = new ArrayList<>();
        subtasksForMoving.add(firstSubtaskForMoving);
        subtasksForMoving.add(secondSubtaskForMoving);
        List<Subtask> subtasksForTravel = new ArrayList<>();
        subtasksForTravel.add(subtaskForTravel);
        Epic firstEpic = new Epic("Понедельник", "", 987, Status.NEW, subtasksForMoving);
        Epic secondEpic = new Epic("Путешествие", "Организовать", 907, Status.IN_PROGRESS,
                subtasksForTravel);
        taskManager.addTask(firstTask);
        taskManager.addTask(secondTask);
        taskManager.addTask(thirdTask);
        taskManager.addTask(lastTask);
        taskManager.addTask(firstSubtaskForMoving);
        taskManager.addTask(secondSubtaskForMoving);
        taskManager.addTask(subtaskForTravel);
        taskManager.addTask(firstEpic);
        taskManager.getAllTasks();
        System.out.println("Выберите новый статус для задачи: 1 - NEW, 2 - IN_PROGRESS, 3 - DONE");
        int numberForNewStatus = scanner.nextInt();
        taskManager.changeTaskStatus(subtaskForTravel, numberForNewStatus);
        taskManager.changeEpicStatus(secondEpic);
        System.out.println(secondEpic);
        taskManager.getTaskById(123);
        taskManager.removeTask(68);
        taskManager.getAllTasks();
        System.out.println("Введите taskId задачи, которую нужно обновить:");
        int taskId = scanner.nextInt();
        System.out.println("Выберите, что хотите поменять: 1 - название, 2 - описание");
        int updateType = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Введите новое значение");
        String newValue = scanner.nextLine();
        taskManager.updateTask(updateType, taskId, newValue);
        taskManager.getAllTasks();
        taskManager.getSubtasksForEpic(firstEpic);
        taskManager.removeAllTasks();
        taskManager.getAllTasks();
    }
}
