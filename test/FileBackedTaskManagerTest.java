import managers.FileBackedTaskManager;
import tasks.Epic;
import tasks.Task;

import java.time.LocalDateTime;
import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;


import java.io.*;


public class FileBackedTaskManagerTest {

    private FileBackedTaskManager taskManager;
    private File file;

    @BeforeEach
    public void setup() throws IOException {
        file = new File("test_tasks.csv");
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    public void testAddTask() throws IOException {
        Task task = new Task("Задача", "Описание", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getName(), retrievedTask.getName());
    }

    @Test
    public void testAddEpic() throws IOException {
        Epic epic = new Epic("Эпик", "Описание", Duration.ofHours(5), LocalDateTime.now());
        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic.getName(), retrievedEpic.getName());
    }

    @Test
    public void testUpdateTask() throws IOException {
        Task task = new Task("Задача", "Описание", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task);

        task.setName("Обновленное задание");
        taskManager.update(task);

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertNotNull(updatedTask);
        assertEquals("Обновленное задание", updatedTask.getName());
    }

    @Test
    public void testClearAllTasks() throws IOException {
        Task task1 = new Task("Задача 1", "Описание", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание", Duration.ofHours(3), LocalDateTime.now());

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.clearAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void testSaveToFile() throws IOException {
        Task task = new Task("Задача", "Описание", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        reader.readLine();
        String line = reader.readLine();
        assertNotNull(line);
        assertTrue(line.contains("Задача"));
        reader.close();
    }

    @Test
    public void testLoadFromFile() throws IOException {
        // Сначала добавляем задачи в менеджер
        Task task1 = new Task("Задача", "Описание", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task1);
        taskManager.save();

        FileBackedTaskManager newTaskManager = new FileBackedTaskManager(file);
        Task loadedTask = newTaskManager.getTaskById(task1.getId());

        assertNotNull(loadedTask);
        assertEquals(task1.getName(), loadedTask.getName());
    }

    @Test
    public void testDeleteTaskById() throws IOException {
        Task task = new Task("Задача", "Описание", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task);

        taskManager.clearById(task.getId());

        Task deletedTask = taskManager.getTaskById(task.getId());
        assertNull(deletedTask);
    }

    @Test
    public void testFilePersistence() throws IOException {
        Task task = new Task("Задача", "Описание", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task);

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        assertNotNull(line);
        boolean foundTask = false;
        while ((line = reader.readLine()) != null) {
            if (line.contains("Задача")) {
                foundTask = true;
                break;
            }
        }
        reader.close();
        assertTrue(foundTask);
    }

    @Test
    public void testClearAllEpics() throws IOException {
        Epic epic = new Epic("Эпик", "Описание", Duration.ofHours(5), LocalDateTime.now());
        taskManager.addEpic(epic);

        taskManager.clearAllEpics();

        assertTrue(taskManager.getAllEpics().isEmpty());
    }


    @Test
    public void testCheckTaskOverlap() {
        Task task1 = new Task("Задача 1", "Описание", Duration.ofHours(2), LocalDateTime.of(2025, 3, 20, 10, 0));
        Task task2 = new Task("Задача 2", "Описание", Duration.ofHours(2), LocalDateTime.of(2025, 3, 20, 11, 0));

        boolean isOverlapping = taskManager.checkTaskOverlap(task1, task2);
        assertTrue(isOverlapping);

        Task task3 = new Task("Задача 3", "Описание", Duration.ofHours(1), LocalDateTime.of(2025, 3, 20, 13, 0));
        isOverlapping = taskManager.checkTaskOverlap(task1, task3);
        assertFalse(isOverlapping);
    }
}