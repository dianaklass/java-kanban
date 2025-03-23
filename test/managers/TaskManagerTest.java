/*import tasks.Epic;
import tasks.Task;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setup() {
        taskManager = createTaskManager();
    }

    @Test
    public void testAddTask() {
        Task task = new Task("Задача", "Описание", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getName(), retrievedTask.getName());
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic("Эпик", "Описание", Duration.ofHours(5), LocalDateTime.now());
        taskManager.addEpic(epic);

        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic.getName(), retrievedEpic.getName());
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Задача", "Описание", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task);

        task.setName("Обновленная задача");
        taskManager.update(task);

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertNotNull(updatedTask);
        assertEquals("Обновленная задача", updatedTask.getName());
    }

    @Test
    public void testClearAllTasks() {
        Task task1 = new Task("Задача 1", "Описание", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание", Duration.ofHours(3), LocalDateTime.now());

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.clearAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task("Задача 1", "Описание", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание", Duration.ofHours(3), LocalDateTime.now());

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> tasks = taskManager.getAllTasks();
        assertEquals(2, tasks.size());
    }
} */
