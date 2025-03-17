package ru.yandex.practicum.main;


import ru.yandex.practicum.main.model.Epic;

import ru.yandex.practicum.main.model.Status;

import ru.yandex.practicum.main.model.SubTask;

import ru.yandex.practicum.main.model.Task;

import ru.yandex.practicum.main.service.Manager;

import ru.yandex.practicum.main.service.TaskManager;

import ru.yandex.practicum.main.service.HistoryManager;


import java.util.List;


public class Main {


    public static void main(String[] args) {

        TaskManager taskManager = Manager.getDefault();

        Task task1 = new Task("Купить продукты на ужин", "Сделать заказ в Яндекс Еда");

        Task task2 = new Task("Поехать на день рождения", "Заказать такси в Яндекс Такси");

        Epic epic1 = new Epic("Поехать на море", "Подготовить все к поездке");

        SubTask subTask1 = new SubTask("Купить билеты", "Выбрать компанию перелетов");

        SubTask subTask2 = new SubTask("Собрать вещи", "Взять одежду, умывалки");

        Epic epic2 = new Epic("Пройти первый модуль", "закрыть все пять тем");

        SubTask subTask3 = new SubTask("Сдать финальные проекты", "Прочитать теорию");

        taskManager.addTask(task1);

        taskManager.addTask(task2);

        taskManager.addTask(epic1);

        taskManager.addTask(epic2);

        taskManager.addTask(subTask1);

        taskManager.addTask(subTask2);

        taskManager.addTask(subTask3);

        epic1.addSubTask(subTask1);

        epic1.addSubTask(subTask2);

        epic2.addSubTask(subTask3);


        taskManager.getAllTasks();

        taskManager.getAllEpics();

        taskManager.getAllSubTasks();


        taskManager.getTaskById(3);


        taskManager.clearById(1);


        Task newTask = new Task("Купить хлеб", "Пойти в пятерочку");

        newTask.setId(3);

        taskManager.update(newTask);

        taskManager.getAllTasks();

        taskManager.getAllEpics();

        taskManager.getAllSubTasks();


        taskManager.getSubTaskList(3);


        subTask1.setStatus(Status.DONE);

        subTask2.setStatus(Status.DONE);

        taskManager.updateEpicStatus(epic1);


        System.out.println("Статус эпика после обновления: " + epic1.getStatus());


        taskManager.getTaskById(5);

        taskManager.getTaskById(2);

        taskManager.getTaskById(7);


        List<Task> history = Manager.getDefaultHistory().getHistory();


        taskManager.clearAllTasks();


        taskManager.getAllTasks();

        taskManager.getAllEpics();

        taskManager.getAllSubTasks();


    }


} 