package main.java.ru.yandex.practicum.main.service;

import main.java.ru.yandex.practicum.main.model.Epic;
import main.java.ru.yandex.practicum.main.model.Status;
import main.java.ru.yandex.practicum.main.model.SubTask;
import main.java.ru.yandex.practicum.main.model.Task;

import java.util.List;

public interface TaskManager {

    // Методы для работы с задачами
    void addTask(Task task);

    List<Task> getAllTasks();

    Task getTaskById(int id);

    void clearAllTasks();

    void clearById(int id);

    void update(Task newTask);

    // Методы для работы с эпиками
    void addEpic(Epic epic);

    List<Epic> getAllEpics();

    Epic getEpicById(int id);

    void updateEpic(Epic epic);

    void clearAllEpics();

    // Методы для работы с подзадачами
    void addSubTask(SubTask subTask);

    List<SubTask> getAllSubTasks();

    List<SubTask> getSubTaskList(int epicId);

    void deleteSubTaskById(int id);

    void deleteAllSubTasks();

    void updateSubTaskStatus(SubTask subTask, Status newStatus);


    List<Task> getHistory();


    void updateEpicStatus(Epic epic);


    default void printError(String message) {
        System.out.println("Ошибка: " + message);
    }
}