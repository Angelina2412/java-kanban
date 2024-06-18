import org.junit.jupiter.api.Assertions;
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
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getEpicById(3));
    }

    @Test
    void checkSizeInHistory() {
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(6, viewedTasks.size());
    }

    @Test
    void checkSizeInHistoryThenViewedTenRepeated() {
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(6));
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(6, viewedTasks.size());
    }

    @Test
    void checkSizeInHistoryThenViewedElevenTasks() {
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(6));
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(5));
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(6, viewedTasks.size());
    }

    @Test
    void checkOrderInHistory() {
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(1, viewedTasks.get(0).getTaskId());
        assertEquals(4, viewedTasks.get(1).getTaskId());
        assertEquals(5, viewedTasks.get(2).getTaskId());
        assertEquals(6, viewedTasks.get(3).getTaskId());
        assertEquals(2, viewedTasks.get(4).getTaskId());
        assertEquals(3, viewedTasks.get(5).getTaskId());
    }

    @Test
    void checkRemoveTaskFromHistory() {
        inMemoryHistoryManager.remove(5);
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(5, viewedTasks.size());
    }

    @Test
    void checkRemoveNonExistentTask() {
        inMemoryHistoryManager.remove(23);
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(6, viewedTasks.size());
    }

    @Test
    void checkEmptyHistory() {
        InMemoryHistoryManager emptyHistoryManager = new InMemoryHistoryManager();
        List<Task> viewedTasks = emptyHistoryManager.getHistory();
        assertEquals(0, viewedTasks.size());
    }

    @Test
    void checkLinkedListIntegrity() {
        Node current = inMemoryHistoryManager.getHead();
        while (current != null && current.getNext() != null) {
            assertEquals(current, current.getNext().getPrev());
            current = current.getNext();
        }
    }
}
