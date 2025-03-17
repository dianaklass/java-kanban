package ru.yandex.practicum.main.service;

import org.junit.jupiter.api.Test;

import ru.yandex.practicum.main.model.Epic;

import ru.yandex.practicum.main.model.Status;

import ru.yandex.practicum.main.model.SubTask;

import ru.yandex.practicum.main.model.Task;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void testTasksEqualityById() {


        Task task1 = new Task("1 задание", "блабла");

        Task task2 = new Task("2 задание", "блабла");

        task1.setId(1);

        task2.setId(1);

        assertEquals(task1, task2, "Задания с одним айди должны быть идентичными");
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

        TaskManager taskManager = Manager.getDefault();
        assertNotNull(taskManager, "Менеджер по умолчнию должен быть готов к использованию");

    }

    @Test
    void testAddingAndRetrievingTasks() {

        TaskManager taskManager = Manager.getDefault();

        Task task = new Task("1 задание", "блабла");

        taskManager.addTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertEquals(task, retrievedTask, "Задача должна быь доступна для поиска по ее идентификатору");

    }

    @Test
    void testTaskIdConflictPrevention() {
        TaskManager taskManager = Manager.getDefault();
        Task taskWithId = new Task("1 задание", "блабла");
        taskWithId.setId(10);
        taskManager.addTask(taskWithId);
        Task generatedTask = new Task("2 задание", "блабла");


        taskManager.addTask(generatedTask);

        assertNotEquals(taskWithId.getId(), generatedTask.getId(), "Задания с разным айди не могут быть равны");

    }

    @Test
    void testTaskImmutabilityAfterAddition() {

        TaskManager taskManager = Manager.getDefault();

        Task task = new Task("1 задание", "блабла");

        taskManager.addTask(task);


        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertEquals(task.getName(), retrievedTask.getName(), "Название должно оставаться прежним");

        assertEquals(task.getDescription(), retrievedTask.getDescription(), "Описание должно оставаться прежним");

        assertEquals(task.getStatus(), retrievedTask.getStatus(), "Статус задачи должен оставаться прежним");

    }

    @Test
    void testHistoryManagerPreservesTaskVersions() {

        HistoryManager historyManager = Manager.getDefaultHistory();
        Task task = new Task("1 задание", "блабла");
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "История состоит из одного задания");
        assertEquals(task, history.get(0), "Задача в истории должна равняться добавленному зданию");

    }
}


