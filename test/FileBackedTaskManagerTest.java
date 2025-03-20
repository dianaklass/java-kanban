import managers.FileBackedTaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import java.time.LocalDateTime;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @Test
    void testEpicStatusAllNew() {
        try {
            File tempFile = File.createTempFile("task_manager", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            // Исправленный конструктор для Epic
            Epic epic = new Epic("Epic 1", "Description of Epic", Duration.ofHours(5), LocalDateTime.now());
            manager.addEpic(epic);

            // Исправленный конструктор для SubTask
            SubTask subTask1 = new SubTask("SubTask 1", "Description 1", Duration.ofHours(1), LocalDateTime.now(), epic, Status.NEW);
            manager.addSubTask(subTask1);

            SubTask subTask2 = new SubTask("SubTask 2", "Description 2", Duration.ofHours(1), LocalDateTime.now(), epic, Status.NEW);
            manager.addSubTask(subTask2);

            assertEquals(Status.NEW, epic.getStatus(), "Epic status should be NEW when all subtasks are NEW");
        } catch (IOException e) {
            e.printStackTrace();
            fail("An IOException occurred while creating a temporary file.");
        }
    }


    @Test
    void testEpicStatusAllDone() {
        try {
            File tempFile = File.createTempFile("task_manager", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            Epic epic = new Epic("Epic 1", "Description of Epic", Duration.ofHours(5), LocalDateTime.now());
            manager.addEpic(epic);

            SubTask subTask1 = new SubTask("SubTask 1", "Description 1", Duration.ofHours(1), LocalDateTime.now(), epic, Status.DONE);
            manager.addSubTask(subTask1);

            SubTask subTask2 = new SubTask("SubTask 2", "Description 2", Duration.ofHours(1), LocalDateTime.now(), epic, Status.DONE);
            manager.addSubTask(subTask2);

            assertEquals(Status.DONE, epic.getStatus(), "Epic status should be DONE when all subtasks are DONE");
        } catch (IOException e) {
            e.printStackTrace();
            fail("An IOException occurred while creating a temporary file.");
        }
    }

    @Test
    void testEpicStatusMixed() {
        try {
            File tempFile = File.createTempFile("task_manager", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            Epic epic = new Epic("Epic 1", "Description of Epic", Duration.ofHours(5), LocalDateTime.now());
            manager.addEpic(epic);

            SubTask subTask1 = new SubTask("SubTask 1", "Description 1", Duration.ofHours(1), LocalDateTime.now(), epic, Status.NEW);
            manager.addSubTask(subTask1);

            SubTask subTask2 = new SubTask("SubTask 2", "Description 2", Duration.ofHours(1), LocalDateTime.now(), epic, Status.DONE);
            manager.addSubTask(subTask2);

            assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status should be IN_PROGRESS when subtasks have different statuses");
        } catch (IOException e) {
            e.printStackTrace();
            fail("An IOException occurred while creating a temporary file.");
        }
    }


    @Test
    void testEpicStatusInProgress() {
        try {
            File tempFile = File.createTempFile("task_manager", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            Epic epic = new Epic("Epic 1", "Description of Epic", Duration.ofHours(5), LocalDateTime.now());
            manager.addEpic(epic);

            SubTask subTask1 = new SubTask("SubTask 1", "Description 1", Duration.ofHours(1), LocalDateTime.now(), epic, Status.IN_PROGRESS);
            manager.addSubTask(subTask1);

            assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Epic status should be IN_PROGRESS when a subtask is in progress");
        } catch (IOException e) {
            e.printStackTrace();
            fail("An IOException occurred while creating a temporary file.");
        }
    }

    @Test
    void testTaskTimeIntervalsOverlap() {
        try {
            File tempFile = File.createTempFile("task_manager", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

            LocalDateTime startTime1 = LocalDateTime.of(2025, 3, 20, 1, 0);
            Task task1 = new Task("Task 1", "Description 1", Duration.ofHours(1), startTime1);

            LocalDateTime startTime2 = LocalDateTime.of(2025, 3, 20, 1, 30);
            Task task2 = new Task("Task 2", "Description 2", Duration.ofHours(1), startTime2);

            manager.addTask(task1);
            manager.addTask(task2);

            assertTrue(manager.checkTaskOverlap(task1, task2), "Tasks should overlap in time");

            LocalDateTime startTime3 = LocalDateTime.of(2025, 3, 20, 3, 0);
            Task task3 = new Task("Task 3", "Description 3", Duration.ofHours(1), startTime3);

            assertFalse(manager.checkTaskOverlap(task1, task3), "Tasks should not overlap in time");
        } catch (IOException e) {
            e.printStackTrace();
            fail("An IOException occurred while creating a temporary file.");
        }
    }

    @Test
    void testExceptionOnFileNotFound() {
        assertThrows(IOException.class, () -> {
            // Исправление: передаем объект File вместо строки
            File nonExistingFile = new File("non_existing_file.csv");
            FileBackedTaskManager.loadFromFile(nonExistingFile);
        }, "Attempting to load a non-existing file should throw an IOException");
    }

    @Test
    void testSaveToFileWithoutException() {
        try {
            File tempFile = File.createTempFile("task_manager", ".csv");
            tempFile.deleteOnExit();

            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
            assertDoesNotThrow(() -> {
                manager.save();
            }, "Saving to a file should not throw any exception");
        } catch (IOException e) {
            e.printStackTrace();
            fail("An IOException occurred while creating a temporary file.");
        }
    }
}