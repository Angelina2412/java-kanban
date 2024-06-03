import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {

    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @BeforeEach
    void createSomething() {
        inMemoryTaskManager.createTask("Прогулка", "С собакой", Status.NEW);
        inMemoryTaskManager.createSubtask("Заказать еду", "Perekrestok", Status.IN_PROGRESS);
        inMemoryTaskManager.createEpic("План на понедельник", "Успеть до обеда", Status.NEW);
        inMemoryTaskManager.createTask("Обед", "Дома", Status.NEW);
        inMemoryTaskManager.createTask("Ужин", "Кафе", Status.NEW);
        inMemoryTaskManager.createTask("Завтрак", "Шаурмечная", Status.NEW);
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(1));
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(4));
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(5));
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(6));
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(4));
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(5));
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(6));
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getSubtaskById(2));
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getEpicById(3));
    }

    @Test
    void checkSizeInHistory() {
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(9, viewedTasks.size());
    }

    @Test
    void checkSizeInHistoryThenViewedTenTasks() {
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(6));
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(10, viewedTasks.size());
    }

    @Test
    void checkSizeInHistoryThenViewedElevenTasks() {
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(6));
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(5));
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(10, viewedTasks.size());
    }

}
