package ru.yandex.practicum.main.service;


import ru.yandex.practicum.main.model.Task;


import java.util.List;


public interface HistoryManager {


    void add(Task task);


    List<Task> getHistory();


}
