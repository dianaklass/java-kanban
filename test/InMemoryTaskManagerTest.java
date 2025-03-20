import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class InMemoryTaskManagerTest {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    public void setup() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void testAddTask() {
        Task task = new Task("Test Task", "Description", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getName(), retrievedTask.getName());
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic("Test Epic", "Epic Description", Duration.ofHours(5), LocalDateTime.now());
        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic.getName(), retrievedEpic.getName());
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Test Task", "Description", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task);

        task.setName("Updated Task");
        taskManager.update(task);

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getName());
    }

    @Test
    public void testClearAllTasks() {
        Task task1 = new Task("Test Task 1", "Description", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("Test Task 2", "Description", Duration.ofHours(3), LocalDateTime.now());

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.clearAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task("Test Task 1", "Description", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("Test Task 2", "Description", Duration.ofHours(3), LocalDateTime.now());

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
    }


}

