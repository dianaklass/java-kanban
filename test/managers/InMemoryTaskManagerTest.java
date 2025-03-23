/* import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import managers.HistoryManager;
import tasks.Status;
import tasks.SubTask;

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

        task.setName("Updated Task");
        taskManager.update(task);

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getName());
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
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Задача 1", "Описание", Duration.ofHours(2), now);

        Task task2 = new Task("Задача 2", "Описание", Duration.ofHours(3), now.plusHours(3));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(2, tasks.size());
    }

    @Test
    void testEpicWithNoSubTasks() {
        Epic epic = new Epic("Эпик", "Эпик без сабтаска", Duration.ofHours(5), LocalDateTime.now());
        epic.setSubTasks(List.of());

        epic.updateEpicData();
        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если нет подзадач");
    }

    @Test
    void testEpicWithAllSubTasksNew() {
        Epic epic = new Epic("Эпик", "Эпик без сабтаска", Duration.ofHours(5), LocalDateTime.now());

        SubTask subTask1 = new SubTask("Сабтаск 1", "Описание", Duration.ofHours(2), LocalDateTime.now(), epic, Status.NEW);
        SubTask subTask2 = new SubTask("Сабтаск 2", "Описание", Duration.ofHours(3), LocalDateTime.now().plusHours(1), epic, Status.NEW);

        epic.setSubTasks(List.of(subTask1, subTask2));

        epic.updateEpicData();

        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если все подзадачи NEW");
    }

    @Test
    void testEpicWithAllSubTasksDone() {
        Epic epic = new Epic("Эпик", "Эпик с готовыми задачами", Duration.ofHours(5), LocalDateTime.now());

        SubTask subTask1 = new SubTask("Сабтаск 1", "Описание", Duration.ofHours(2), LocalDateTime.now(), epic, Status.DONE);
        SubTask subTask2 = new SubTask("Сабтаск 2", "Описание", Duration.ofHours(3), LocalDateTime.now().plusHours(1), epic, Status.DONE);

        epic.setSubTasks(List.of(subTask1, subTask2));

        epic.updateEpicData();

        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, если все подзадачи DONE");
    }

    @Test
    void testEpicWithSubTasksNewAndDone() {
        Epic epic = new Epic("Эпик", "Эпик с готовыми сабтасками", Duration.ofHours(5), LocalDateTime.now());

        SubTask subTask1 = new SubTask("Сабтаск 1", "Описание", Duration.ofHours(2), LocalDateTime.now(), epic, Status.NEW);
        SubTask subTask2 = new SubTask("Сабтаск 2", "Описание", Duration.ofHours(3), LocalDateTime.now().plusHours(1), epic, Status.DONE);

        epic.setSubTasks(List.of(subTask1, subTask2));

        epic.updateEpicData();

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, если есть подзадачи с разными статусами");
    }

    @Test
    void testEpicWithSubTasksInProgress() {
        Epic epic = new Epic("Эпик", "Эпик с сабтасками в прогрессе", Duration.ofHours(5), LocalDateTime.now());

        SubTask subTask1 = new SubTask("Сабтаск 1", "Описание", Duration.ofHours(2), LocalDateTime.now(), epic, Status.IN_PROGRESS);
        SubTask subTask2 = new SubTask("Сабтаск 2", "Описание", Duration.ofHours(3), LocalDateTime.now().plusHours(1), epic, Status.IN_PROGRESS);

        epic.setSubTasks(List.of(subTask1, subTask2));

        epic.updateEpicData();

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, если подзадачи находятся в процессе");
    }

    @Test
    void testHistoryManagerWithNoTasks() {
        HistoryManager historyManager = taskManager.getHistoryManager();
        assertTrue(historyManager.getHistory().isEmpty(), "История задач должна быть пуста, если задач не было");
    }

    @Test
    void testHistoryManagerWithDuplicateTasks() {
        Task task = new Task("Задача", "Описание", Duration.ofHours(2), LocalDateTime.now());
        taskManager.addTask(task);

        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());

        HistoryManager historyManager = taskManager.getHistoryManager();
        assertEquals(1, historyManager.getHistory().size(), "История должна содержать только одну задачу, даже если она добавлена несколько раз");
    }

    @Test
    void testHistoryManagerRemoveFromStart() {
        Task task1 = new Task("Задача 1", "Описание", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание", Duration.ofHours(3), LocalDateTime.now().plusHours(1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        HistoryManager historyManager = taskManager.getHistoryManager();
        taskManager.clearById(task1.getId());

        assertEquals(1, historyManager.getHistory().size(), "История должна содержать одну задачу после удаления первой из двух");
    }

    @Test
    void testHistoryManagerRemoveFromEnd() {
        Task task1 = new Task("Задача 1", "Описание", Duration.ofHours(2), LocalDateTime.now());
        Task task2 = new Task("Задача 2", "Описание", Duration.ofHours(3), LocalDateTime.now().plusHours(1));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        HistoryManager historyManager = taskManager.getHistoryManager();
        taskManager.clearById(task2.getId());

        assertEquals(1, historyManager.getHistory().size(), "История должна содержать одну задачу после удаления последней");
    }


} */


