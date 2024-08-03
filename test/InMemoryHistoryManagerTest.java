import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {

    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @BeforeEach
    void createSomething() {
        Task task1 = inMemoryTaskManager.createTask("Прогулка", "С собакой", Status.NEW,
                Duration.ofHours(1), LocalDateTime.now().plusHours(0));
        Subtask subtask2 = inMemoryTaskManager.createSubtask("Заказать еду", "Perekrestok",
                Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        Epic epic3 = inMemoryTaskManager.createEpic("План на понедельник", "Успеть до обеда",
                Status.NEW);
        Task task4 = inMemoryTaskManager.createTask("Обед", "Дома", Status.NEW,
                Duration.ofHours(1), LocalDateTime.now().plusHours(6));
        Task task5 = inMemoryTaskManager.createTask("Ужин", "Кафе", Status.NEW,
                Duration.ofHours(1), LocalDateTime.now().plusHours(8));
        Task task6 = inMemoryTaskManager.createTask("Завтрак", "Шаурмечная", Status.NEW,
                Duration.ofHours(1), LocalDateTime.now().plusHours(10));

        inMemoryHistoryManager.addTask(task1);
        inMemoryHistoryManager.addTask(task4);
        inMemoryHistoryManager.addTask(task5);
        inMemoryHistoryManager.addTask(task6);
        inMemoryHistoryManager.addTask(task4);
        inMemoryHistoryManager.addTask(task5);
        inMemoryHistoryManager.addTask(task6);
        inMemoryHistoryManager.addTask(subtask2);
        inMemoryHistoryManager.addTask(epic3);
        inMemoryHistoryManager.addTask(epic3);
    }


    @Test
    void checkSizeInHistory() {
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(6, viewedTasks.size());
    }

    @Test
    void checkSizeInHistoryThenViewedRepeated() {
        inMemoryHistoryManager.addTask(inMemoryTaskManager.getTaskById(6));
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(6, viewedTasks.size());
    }

    @Test
    void checkSizeInHistoryThenViewedRepeatedTwice() {
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
    void checkRemoveFirstTaskFromHistory() {
        inMemoryHistoryManager.remove(1);
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(5, viewedTasks.size());
        assertEquals(4, viewedTasks.get(0).getTaskId());
        assertEquals(5, viewedTasks.get(1).getTaskId());
        assertEquals(6, viewedTasks.get(2).getTaskId());
        assertEquals(2, viewedTasks.get(3).getTaskId());
        assertEquals(3, viewedTasks.get(4).getTaskId());
    }

    @Test
    void checkRemoveMiddleTaskFromHistory() {
        inMemoryHistoryManager.remove(5);
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(5, viewedTasks.size());
        assertEquals(1, viewedTasks.get(0).getTaskId());
        assertEquals(4, viewedTasks.get(1).getTaskId());
        assertEquals(6, viewedTasks.get(2).getTaskId());
        assertEquals(2, viewedTasks.get(3).getTaskId());
        assertEquals(3, viewedTasks.get(4).getTaskId());
    }

    @Test
    void checkRemoveTaskInBottomFromHistory() {
        inMemoryHistoryManager.remove(3);
        List<Task> viewedTasks = inMemoryHistoryManager.getHistory();
        assertEquals(5, viewedTasks.size());
        assertEquals(1, viewedTasks.get(0).getTaskId());
        assertEquals(4, viewedTasks.get(1).getTaskId());
        assertEquals(5, viewedTasks.get(2).getTaskId());
        assertEquals(6, viewedTasks.get(3).getTaskId());
        assertEquals(2, viewedTasks.get(4).getTaskId());
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
