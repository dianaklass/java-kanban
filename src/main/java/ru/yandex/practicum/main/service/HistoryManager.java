package main.java.ru.yandex.practicum.main.service;

import main.java.ru.yandex.practicum.main.model.Task;

import java.util.List;

public interface HistoryManager {
    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}