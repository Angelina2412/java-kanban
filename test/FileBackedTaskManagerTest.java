import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
        manager.addTask(new Task("Понедельник", "Создать задачу", 1, Status.NEW));
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertFalse(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpic().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void testSaveAndLoadMultipleTasks() {
        Task task1 = new Task("Task 1", "Обед", 1, Status.NEW);
        Task task2 = new Task("Task 2", "Ужин", 2, Status.IN_PROGRESS);
        Epic epic1 = new Epic("Epic 1", "Завтрак", 3, Status.NEW, List.of());
        Subtask subtask1 = new Subtask("Subtask 1", "Перекус", 4, Status.DONE);

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
                "id,type,name,status,description,epic",
                "1,TASK,Завтрак,NEW,Description 1,",
                "2,TASK,Обед,IN_PROGRESS,Description 2,",
                "3,EPIC,Ужин,NEW,Description 3,",
                "4,SUBTASK,Салатик,DONE,Description 4,3"
        );
        Files.write(tempFile.toPath(), lines);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(2, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpic().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        Task task1 = new Task("Task 1", "Завтрак", 1, Status.NEW);
        Task task2 = new Task("Task 2", "Обед", 2, Status.IN_PROGRESS);
        Epic epic1 = new Epic("Epic 1", "Ужин", 3, Status.NEW, List.of());
        Subtask subtask1 = new Subtask("Subtask 1", "Салатик", 4, Status.DONE);

        assertEquals(task1, loadedManager.getTaskById(1));
        assertEquals(task2, loadedManager.getTaskById(2));
        assertEquals(epic1, loadedManager.getEpicById(3));
        assertEquals(subtask1, loadedManager.getSubtaskById(4));
    }
}
