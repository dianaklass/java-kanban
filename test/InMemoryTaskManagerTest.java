import managers.InMemoryTaskManager;
import managers.Manager;
import managers.TaskManager;
import managers.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Manager.getDefault(); // Инициализация taskManager
    }

    @Test
    void testAddingAndRetrievingTasks() {
        Task task = new Task("1 задание", "блабла", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask, "Задача должна быть доступна для поиска по её идентификатору");
    }

    @Test
    void testEpicStatusAllNew() {
        Epic epic = new Epic("Epic 1", "Description of Epic", Duration.ofMinutes(120), LocalDateTime.now());
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.now(), epic, Status.NEW);
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(40), epic, Status.NEW);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.NEW, epic.getStatus(), "Статус эпика должен быть NEW, если все подзадачи в статусе NEW");
    }

    @Test
    void testEpicStatusAllDone() {
        Epic epic = new Epic("Epic 1", "Description of Epic", Duration.ofMinutes(120), LocalDateTime.now());
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.now(), epic, Status.DONE);
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(40), epic, Status.DONE);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.DONE, epic.getStatus(), "Статус эпика должен быть DONE, если все подзадачи в статусе DONE");
    }

    @Test
    void testEpicStatusMixed() {
        Epic epic = new Epic("Epic 1", "Description of Epic", Duration.ofMinutes(120), LocalDateTime.now());
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.now(), epic, Status.NEW);
        taskManager.addSubTask(subTask1);

        SubTask subTask2 = new SubTask("SubTask 2", "Description 2", Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(40), epic, Status.DONE);
        taskManager.addSubTask(subTask2);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, если подзадачи имеют разные статусы");
    }

    @Test
    void testEpicStatusInProgress() {
        Epic epic = new Epic("Epic 1", "Description of Epic", Duration.ofMinutes(120), LocalDateTime.now());
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("SubTask 1", "Description 1", Duration.ofMinutes(30), LocalDateTime.now(), epic, Status.IN_PROGRESS);
        taskManager.addSubTask(subTask1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus(), "Статус эпика должен быть IN_PROGRESS, если хотя бы одна подзадача в статусе IN_PROGRESS");
    }

    @Test
    void testHistoryManagerPreservesTaskVersions() {
        HistoryManager historyManager = Manager.getDefaultHistory();
        Task task = new Task("1 задание", "блабла", Duration.ofMinutes(30), LocalDateTime.now());
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История состоит из одного задания");
        assertEquals(task, history.get(0), "Задача в истории должна равняться добавленному заданию");
    }

    @Test
    void testHistoryManagerAddAndGetHistory() {
        HistoryManager historyManager = Manager.getDefaultHistory();
        Task task = new Task("Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной задачей");
    }
}

