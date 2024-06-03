import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> viewedTasks;

    public InMemoryHistoryManager() {
        viewedTasks = new ArrayList<>();
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            if (viewedTasks.size() < 10) {
                viewedTasks.add(task);
            } else {
                viewedTasks.remove(0);
                viewedTasks.add(task);
            }
        }
    }
    @Override
    public List<Task> getHistory() {
        for (Task task : viewedTasks) {
            System.out.println("ID задачи: " + task.getTaskId());
            System.out.println("Название задачи: " + task.getTaskName());
            System.out.println("Статус задачи: " + task.getStatus());
            System.out.println();
        }
        return viewedTasks;
    }
}
