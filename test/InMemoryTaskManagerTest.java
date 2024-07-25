import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @BeforeEach
    void createTask() {
        inMemoryTaskManager.clear();
        inMemoryTaskManager.createTask("Прогулка", "С собакой", Status.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(0));
        inMemoryTaskManager.createTask("Прогулка", "С подругой", Status.IN_PROGRESS, 11, Duration.ofHours(3), LocalDateTime.now().plusHours(2));
        inMemoryTaskManager.createSubtask("Заказать еду", "Perekrestok", Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.now().plusHours(6));
        inMemoryTaskManager.createSubtask("Заказать воду", "VV", Status.IN_PROGRESS, 12, Duration.ofHours(1), LocalDateTime.now().plusHours(8));

        List<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Подзадача 1", "Описание для подзадачи 1", 13, Status.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(10)));
        subtasks1.add(new Subtask("Подзадача 2", "Описание для подзадачи 2", 14, Status.NEW, Duration.ofHours(2), LocalDateTime.now().plusHours(12)));

        List<Subtask> subtasks2 = new ArrayList<>();
        subtasks2.add(new Subtask("Подзадача 3", "Описание для подзадачи 3", 15, Status.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(10)));
        subtasks2.add(new Subtask("Подзадача 4", "Описание для подзадачи 4", 16, Status.NEW, Duration.ofHours(2), LocalDateTime.now().plusHours(12)));

        inMemoryTaskManager.createEpic("Эпик", "Описание для эпика", Status.NEW, subtasks1, Duration.ZERO, null);
        inMemoryTaskManager.createEpic("Ещё один эпик", "Описание для еще одного эпика", 18, Status.NEW, subtasks2, Duration.ZERO, null);

    }

    @Test
    void checkTaskIntervalOverlap() {
        Task task1 = inMemoryTaskManager.createTask("Задача 1", "Описание 1", Status.NEW, Duration.ofHours(2), LocalDateTime.now().plusHours(31));
        Task task2 = inMemoryTaskManager.createTask("Задача 2", "Описание 2", Status.NEW, Duration.ofHours(2), LocalDateTime.now().plusHours(31).plusMinutes(30));

        boolean isOverlap = task2.getStartTime().isBefore(task1.getEndTime()) && task2.getEndTime().isAfter(task1.getStartTime());
        assertTrue(isOverlap, "Задачи должны пересекаться по времени");
    }

    @Test
    void checkEpicWithSameSubtasksTimeNotCreate() {
        List<Task> allEpics = inMemoryTaskManager.getAllEpic();
        assertEquals(1, allEpics.size());
    }

    @Test
    void checkEpicTimeWithSubtasks() {
        Epic epic = inMemoryTaskManager.getEpicById(3);
        assertNotNull(epic.getStartTime());
        assertEquals(Duration.ofHours(3), epic.getDuration());
    }

    @Test
    void checkEpicWithSameTimeNotCreate() {
        Epic newEpic = inMemoryTaskManager.createEpic("План на вторник", "Успеть до вечера", Status.NEW, Duration.ofHours(3), LocalDateTime.now().plusHours(10));
        List<Task> allEpics = inMemoryTaskManager.getAllEpic();
        assertEquals(1, allEpics.size());
    }

    @Test
    void checkTaskWithSameTimeNotCreate() {
        List<Task> allTasksInTheStart = inMemoryTaskManager.getAllTasks();
        assertEquals(2, allTasksInTheStart.size());
        inMemoryTaskManager.createTask("Таска", "Таска с таким же временем, как у другой таска", Status.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(0));
        List<Task> allTasks = inMemoryTaskManager.getAllTasks();
        assertEquals(2, allTasks.size());
    }

    @Test
    void checkSubtaskWithSameTimeNotCreate() {
        List<Task> allSubtasksInTheStart = inMemoryTaskManager.getAllSubtasks();
        assertEquals(2, allSubtasksInTheStart.size());
        inMemoryTaskManager.createSubtask("Подзадача 3", "Описание для подзадачи 3", Status.NEW, 15, Duration.ofHours(1), LocalDateTime.now().plusHours(10));
        List<Task> allSubtasks = inMemoryTaskManager.getAllSubtasks();
        assertEquals(2, allSubtasks.size());
    }

    @Test
    void shouldReturnNewStatusWhenAllSubtasksAreNew() {
        Epic epicGetSubtask = inMemoryTaskManager.getEpicById(3);
        assertEquals(Status.NEW, epicGetSubtask.getStatus(), "Статус у эпика должен быть NEW");
    }

    @Test
    void shouldReturnDoneStatusWhenAllSubtasksAreDone() {
        List<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Подзадача 3", "Описание для подзадачи 3", 15, Status.DONE, Duration.ofHours(1), LocalDateTime.now().plusHours(14)));
        subtasks1.add(new Subtask("Подзадача 4", "Описание для подзадачи 4", 16, Status.DONE, Duration.ofHours(1), LocalDateTime.now().plusHours(16)));
        inMemoryTaskManager.createEpic("Эпик, где результат Done", "Описание для еще одного эпика", Status.NEW, subtasks1, Duration.ZERO, null);
        Epic epicGetSubtask = inMemoryTaskManager.getEpicById(4);
        assertEquals(Status.DONE, epicGetSubtask.getStatus(), "Статус у эпика должен быть DONE");
    }

    @Test
    void shouldReturnInProgressWhenOneSubtaskDoneAnotherSubtaskNew() {
        List<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Подзадача 5", "Описание для подзадачи 5", 15, Status.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(14)));
        subtasks1.add(new Subtask("Подзадача 6", "Описание для подзадачи 6", 16, Status.DONE, Duration.ofHours(1), LocalDateTime.now().plusHours(16)));
        inMemoryTaskManager.createEpic("Эпик, где результат New", "Описание для еще одного эпика", Status.NEW, subtasks1, Duration.ZERO, null);
        Epic epicGetSubtask = inMemoryTaskManager.getEpicById(4);
        assertEquals(Status.IN_PROGRESS, epicGetSubtask.getStatus(), "Статус у эпика должен быть IN_PROGRESS");
    }

    @Test
    void shouldReturnInProgressWhenSubtasksInProgress() {
        List<Subtask> subtasks1 = new ArrayList<>();
        subtasks1.add(new Subtask("Подзадача 5", "Описание для подзадачи 5", 15, Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.now().plusHours(14)));
        subtasks1.add(new Subtask("Подзадача 6", "Описание для подзадачи 6", 16, Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.now().plusHours(16)));
        inMemoryTaskManager.createEpic("Эпик, где результат New", "Описание для еще одного эпика", Status.NEW, subtasks1, Duration.ZERO, null);
        Epic epicGetSubtask = inMemoryTaskManager.getEpicById(4);
        assertEquals(Status.IN_PROGRESS, epicGetSubtask.getStatus(), "Статус у эпика должен быть IN_PROGRESS");
    }

    @Test
    void checkCorrectInformationAboutTask() {
        Task task = inMemoryTaskManager.getTaskById(1);
        assertEquals("Прогулка", task.getTaskName());
        assertEquals("С собакой", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    void checkCorrectInformationAboutSubtask() {
        Subtask subtask = inMemoryTaskManager.getSubtaskById(2);
        assertEquals("Заказать еду", subtask.getTaskName());
        assertEquals("Perekrestok", subtask.getDescription());
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
    }

    @Test
    void checkCorrectInformationAboutEpic() {
        Epic epic = inMemoryTaskManager.getEpicById(3);
        assertEquals("Эпик", epic.getTaskName());
        assertEquals("Описание для эпика", epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void checkCorrectDeleteTask() {
        inMemoryTaskManager.deleteTaskById(1);
        assertNull(inMemoryTaskManager.getTaskById(1));
    }

    @Test
    void checkCorrectDeleteSubtask() {
        inMemoryTaskManager.deleteSubtaskById(2);
        assertNull(inMemoryTaskManager.getSubtaskById(2));
    }

    @Test
    void checkCorrectDeleteEpic() {
        inMemoryTaskManager.deleteEpicById(3);
        assertNull(inMemoryTaskManager.getEpicById(3));
    }

    @Test
    void checkCorrectUpdateTask() {
        Task newTask = inMemoryTaskManager.createTask("Почитать", "Книгу", Status.IN_PROGRESS, 1, Duration.ofHours(1), LocalDateTime.now());
        inMemoryTaskManager.updateTask(newTask);
        Task task = inMemoryTaskManager.getTaskById(1);
        assertEquals("Почитать", task.getTaskName());
        assertEquals("Книгу", task.getDescription());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void checkCorrectUpdateSubtask() {
        Subtask newSubtask = inMemoryTaskManager.createSubtask("Заказать еду на ужин", "Perekrestok or Samokat", Status.NEW, 2, Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        inMemoryTaskManager.updateSubtask(newSubtask);
        Subtask subtask = inMemoryTaskManager.getSubtaskById(2);
        assertEquals("Заказать еду на ужин", subtask.getTaskName());
        assertEquals("Perekrestok or Samokat", subtask.getDescription());
        assertEquals(Status.NEW, subtask.getStatus());
    }

    @Test
    void checkCorrectUpdateEpic() {
        Epic newEpic = inMemoryTaskManager.createEpic("План на вторник", "Успеть до вечера", 3, Status.IN_PROGRESS, Duration.ZERO, null);
        inMemoryTaskManager.updateEpic(newEpic);
        Epic epic = inMemoryTaskManager.getEpicById(3);
        assertEquals("План на вторник", epic.getTaskName());
        assertEquals("Успеть до вечера", epic.getDescription());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

}



