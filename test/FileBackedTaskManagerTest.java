import managers.FileBackedTaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

public class FileBackedTaskManagerTest {
    public static void main(String[] args) throws IOException {
        File tempFile = File.createTempFile("task_manager", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Задание 1", "Описание первого задания");
        Task task2 = new Task("Задание 2", "Описание второго задания");

        Epic epic1 = new Epic("Эпик 1", "Описание первого эпика");

        manager.addTask(task1);
        manager.addTask(task2);
        manager.addEpic(epic1);

        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1");
        subTask1.setEpic(epic1);
        manager.addSubTask(subTask1);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        System.out.println("Загруженные задачи:");
        System.out.println(loadedManager.getAllTasks());
        System.out.println(loadedManager.getAllEpics());
        System.out.println(loadedManager.getAllSubTasks());

        assert loadedManager.getAllTasks().size() == 2 : "Неверное количество задач";
        assert loadedManager.getAllEpics().size() == 1 : "Неверное количество эпиков";
        assert loadedManager.getAllSubTasks().size() == 1 : "Неверное количество подзадач";
    }
}
