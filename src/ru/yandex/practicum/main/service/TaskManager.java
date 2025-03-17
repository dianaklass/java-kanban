package ru.yandex.practicum.main.service;


import ru.yandex.practicum.main.model.Epic;


import ru.yandex.practicum.main.model.Status;


import ru.yandex.practicum.main.model.SubTask;


import ru.yandex.practicum.main.model.Task;


import java.util.List;


public interface TaskManager {


    void addTask(Task task);


    List<Task> getAllTasks();


    List<Epic> getAllEpics();


    List<SubTask> getAllSubTasks();


    void clearAllTasks();


    void clearAllEpics();


    void clearAllSubTasks();


    Task getTaskById(int id);


    void clearById(int id);


    void update(Task newTask);


    List<SubTask> getSubTaskList(int epicId);


    List<SubTask> updateTaskStatus(Task task, Status newStatus);


    void updateEpicStatus(Epic epic);


    default void printError(String message) {


        System.out.println("Ошибка: " + message);


    }


    List<Task> getHistory();


}


