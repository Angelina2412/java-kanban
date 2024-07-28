import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void deleteAfterTest() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testSaveAndLoadEmptyFile() {
        manager.addTask(new Task("Понедельник", "Создать задачу", 1, Status.NEW, Duration.ofHours(3), LocalDateTime.now().plusHours(2)));
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertFalse(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpic().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testSaveAndLoadMultipleTasks() {
        Task task1 = new Task("Task 1", "Обед", 1, Status.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(2));
        Task task2 = new Task("Task 2", "Ужин", 2, Status.IN_PROGRESS, Duration.ofHours(1), LocalDateTime.now().plusHours(4));
        Epic epic1 = new Epic("Epic 1", "Завтрак", 3, Status.NEW, Duration.ofHours(1), LocalDateTime.now().plusHours(6));
        Subtask subtask1 = new Subtask("Subtask 1", "Перекус", 4, Status.DONE, Duration.ofHours(1), LocalDateTime.now().plusHours(8));

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);
        manager.addSubtask(subtask1);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpic().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        assertEquals(task1, loadedManager.getTaskById(task1.getTaskId()));
        assertEquals(task2, loadedManager.getTaskById(task2.getTaskId()));
        assertEquals(epic1, loadedManager.getEpicById(epic1.getTaskId()));
        assertEquals(subtask1, loadedManager.getSubtaskById(subtask1.getTaskId()));
    }

    @Test
    void testLoadFromFileWithMultipleTasks() throws IOException {
        List<String> lines = List.of(
                "id,type,name,status,description,duration,startTime,epic",
                "1,TASK,Завтрак,NEW,Описание 1,60,2024-07-28T10:00,",
                "2,TASK,Обед,IN_PROGRESS,Описание 2,60,2024-07-28T12:00,",
                "3,EPIC,Ужин,NEW,Описание 3,60,2024-07-28T14:00,",
                "4,SUBTASK,Салатик,DONE,Описание 4,60,2024-07-28T16:00,3"
        );
        Files.write(tempFile.toPath(), lines);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpic().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        Task task1 = new Task("Завтрак", "Описание 1", 1, Status.NEW, Duration.ofMinutes(60), LocalDateTime.parse("2024-07-28T10:00"));
        Task task2 = new Task("Обед", "Описание 2", 2, Status.IN_PROGRESS, Duration.ofMinutes(60), LocalDateTime.parse("2024-07-28T12:00"));
        Epic epic1 = new Epic("Ужин", "Описание 3", 3, Status.NEW, List.of(), Duration.ofMinutes(60), LocalDateTime.parse("2024-07-28T14:00"));
        Subtask subtask1 = new Subtask("Салатик", "Описание 4", 4, Status.DONE, Duration.ofMinutes(60), LocalDateTime.parse("2024-07-28T16:00"));

        assertEquals(task1, loadedManager.getTaskById(1));
        assertEquals(task2, loadedManager.getTaskById(2));
        assertEquals(epic1, loadedManager.getEpicById(3));
        assertEquals(subtask1, loadedManager.getSubtaskById(4));
    }

}
