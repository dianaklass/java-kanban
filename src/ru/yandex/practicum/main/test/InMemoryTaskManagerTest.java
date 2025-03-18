package ru.yandex.practicum.main.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.main.model.Epic;
import ru.yandex.practicum.main.model.Status;
import ru.yandex.practicum.main.model.SubTask;
import ru.yandex.practicum.main.model.Task;
import ru.yandex.practicum.main.service.HistoryManager;
import ru.yandex.practicum.main.service.Manager;
import ru.yandex.practicum.main.service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Manager.getDefault();
    }

    @Test
    void testTasksEqualityById() {
        Task task1 = new Task("1 задание", "блабла");

        task1.setId(1);

        Task task2 = new Task("2 задание", "блабла");

        task2.setId(2);

        assertNotEquals(task1, task2, "Задания с разными ID не могут быть равными");
    }


    @Test
    void testSubtasksAndEpicsEqualityById() {
        Epic epic = new Epic("1 эпик", "блабла");
        SubTask subTask1 = new SubTask("1 подзадача", "блабла 1");
        SubTask subTask2 = new SubTask("2 подзадача", "блабла 2");

        subTask1.setId(1);
        subTask2.setId(1);

        assertEquals(subTask1, subTask2, "Подзадачи с одним айди должны быть равными");
    }

    @Test
    void testEpicCannotBeAddedAsSubtaskToItself() {
        Epic epic = new Epic("1 эпик", "блабла");
        epic.setId(1);
        SubTask subTask = new SubTask("1 подзадача", "блаба 1");

        epic.addSubTask(subTask);
        boolean isEpicAddedAsSubtask = epic.getSubTask().contains(epic);

        assertFalse(isEpicAddedAsSubtask, "Эпик не может быть добавлен как подзадача самого себя");
    }

    @Test
    void testSubtaskCannotSetItselfAsEpic() {
        SubTask subTask = new SubTask("1 подзадача", "блабла");
        subTask.setId(1);

        subTask.setEpic(subTask.getEpic());

        assertNull(subTask.getEpic(), "Подзадача не может сохранять себя как эпик");
    }

    @Test
    void testTaskManagerInitialization() {
        assertNotNull(taskManager, "Менеджер по умолчанию должен быть готов к использованию");
    }

    @Test
    void testAddingAndRetrievingTasks() {
        Task task = new Task("1 задание", "блабла");
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertEquals(task, retrievedTask, "Задача должна быть доступна для поиска по ее идентификатору");
    }

    @Test
    void testTaskIdConflictPrevention() {
        Task taskWithId = new Task("1 задание", "блабла");
        taskWithId.setId(10);
        taskManager.addTask(taskWithId);

        Task generatedTask = new Task("2 задание", "блабла");
        taskManager.addTask(generatedTask);

        assertNotEquals(taskWithId.getId(), generatedTask.getId(), "Задания с разным айди не могут быть равны");
    }

    @Test
    void testTaskImmutabilityAfterAddition() {
        Task task = new Task("1 задание", "блабла");
        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        retrievedTask.setName("Новое имя");
        retrievedTask.setDescription("Новое описание");
        retrievedTask.setStatus(Status.DONE);
        taskManager.update(retrievedTask);
        Task storedTask = taskManager.getTaskById(task.getId());

        assertEquals("Новое имя", storedTask.getName(), "Менеджер должен обновить имя задачи");
        assertEquals("Новое описание", storedTask.getDescription(), "Менеджер должен обновить описание задачи");
        assertEquals(Status.DONE, storedTask.getStatus(), "Менеджер должен обновить статус задачи");
    }


    @Test
    void testHistoryManagerPreservesTaskVersions() {
        HistoryManager historyManager = Manager.getDefaultHistory();
        Task task = new Task("1 задание", "блабла");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "История состоит из одного задания");
        assertEquals(task, history.get(0), "Задача в истории должна равняться добавленному заданию");
    }

    @Test
    void testSubtaskDeletionRemovesItFromEpic() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Подзадача", "Описание подзадачи");
        subTask.setEpic(epic);
        taskManager.addSubTask(subTask);

        taskManager.deleteSubTaskById(subTask.getId());

        assertFalse(epic.getSubTask().contains(subTask), "Эпик не должен содержать удаленную подзадачу");
    }

    @Test
    void testEpicClearsSubtasksWhenAllAreDeleted() {
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);

        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1");
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2");
        subTask1.setEpic(epic);
        subTask2.setEpic(epic);
        taskManager.addSubTask(subTask1);
        taskManager.addSubTask(subTask2);

        taskManager.deleteAllSubTasks();

        assertTrue(epic.getSubTask().isEmpty(), "После удаления всех подзадач эпик должен быть пуст");
    }

    @Test
    void testEpicSubtasksIntegrityAfterUpdate() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.addEpic(epic);

        SubTask subTask = new SubTask("Подзадача", "Описание подзадачи");
        subTask.setEpic(epic);
        taskManager.addSubTask(subTask);

        Epic updatedEpic = new Epic("Обновленный эпик", "Новое описание");
        updatedEpic.setId(epic.getId());
        taskManager.updateEpic(updatedEpic);

        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertTrue(retrievedEpic.getSubTask().contains(subTask), "При обновлении эпика подзадачи должны оставаться привязанными");
    }



}
