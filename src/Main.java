import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Поехали!");
        Task taskFirst = taskManager.createTask("Meeting with friend", "Sasha", Status.NEW);
        taskManager.createTask("Walk with dog", "Evening", Status.IN_PROGRESS);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(taskManager.createSubtask("Reading", "Book", Status.DONE));
        subtasks.add(taskManager.createSubtask("Swimming", "ZIL", Status.IN_PROGRESS));
        Epic epicFirst = taskManager.createEpic("Weekend", "A lot of tasks", Status.NEW, subtasks);
        taskManager.createEpic("Week", "Work", Status.NEW);
        taskManager.createSubtask("Shopping", "Afimoll", Status.IN_PROGRESS);
        taskManager.createSubtask("Cooking", "Dinner", Status.IN_PROGRESS);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(5);
        taskManager.historyManager.getHistory();
    }
}
