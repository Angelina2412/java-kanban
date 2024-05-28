import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InMemoryTaskManagerTest {

    InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

    @BeforeEach
    void createTask(){
        inMemoryTaskManager.createTask("Прогулка", "С собакой", Status.NEW);
        inMemoryTaskManager.createSubtask("Заказать еду",  "Perekrestok", Status.IN_PROGRESS);
        inMemoryTaskManager.createEpic("План на понедельник",  "Успеть до обеда", Status.NEW);
    }

    @Test
    void checkCorrectInformationAboutTask(){
        Task task = inMemoryTaskManager.getTaskById(1);
        assertEquals("Прогулка", task.getTaskName());
        assertEquals("С собакой", task.getDescription());
        assertEquals(Status.NEW, task.getStatus());
    }

    @Test
    void checkCorrectInformationAboutSubtask(){
        Subtask subtask = inMemoryTaskManager.getSubtaskById(2);
        assertEquals("Заказать еду", subtask.getTaskName());
        assertEquals("Perekrestok", subtask.getDescription());
        assertEquals(Status.IN_PROGRESS, subtask.getStatus());
    }

    @Test
    void checkCorrectInformationAboutEpic(){
        Epic epic = inMemoryTaskManager.getEpicById(3);
        assertEquals("План на понедельник", epic.getTaskName());
        assertEquals("Успеть до обеда", epic.getDescription());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void checkCorrectDeleteTask(){
        inMemoryTaskManager.deleteTaskById(1);
        assertNull(inMemoryTaskManager.getTaskById(1));
    }

    @Test
    void checkCorrectDeleteSubtask(){
        inMemoryTaskManager.deleteSubtaskById(2);
        assertNull(inMemoryTaskManager.getSubtaskById(2));
    }

    @Test
    void checkCorrectDeleteEpic(){
        inMemoryTaskManager.deleteEpicById(3);
        assertNull(inMemoryTaskManager.getEpicById(3));
    }

    @Test
    void checkCorrectUpdateTask(){
        Task newTask = inMemoryTaskManager.createTask("Почитать",  "Книгу", Status.IN_PROGRESS, 1);
        inMemoryTaskManager.updateTask(newTask);
        Task task = inMemoryTaskManager.getTaskById(1);
        assertEquals("Почитать", task.getTaskName());
        assertEquals("Книгу", task.getDescription());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
    }

    @Test
    void checkCorrectUpdateSubtask(){
        Subtask newSubtask = inMemoryTaskManager.createSubtask("Заказать еду на ужин",  "Perekrestok or Samokat", Status.NEW, 2);
        inMemoryTaskManager.updateSubtask(newSubtask);
        Subtask subtask = inMemoryTaskManager.getSubtaskById(2);
        assertEquals("Заказать еду на ужин", subtask.getTaskName());
        assertEquals("Perekrestok or Samokat", subtask.getDescription());
        assertEquals(Status.NEW, subtask.getStatus());
    }

    @Test
    void checkCorrectUpdateEpic(){
        Epic newEpic = inMemoryTaskManager.createEpic("План на вторник",  "Успеть до вечера", Status.IN_PROGRESS, 3);
        inMemoryTaskManager.updateEpic(newEpic);
        Epic epic = inMemoryTaskManager.getEpicById(3);
        assertEquals("План на вторник", epic.getTaskName());
        assertEquals("Успеть до вечера", epic.getDescription());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

}
