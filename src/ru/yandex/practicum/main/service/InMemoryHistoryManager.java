package ru.yandex.practicum.main.service;

import ru.yandex.practicum.main.model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();
    private static final int MAX_SIZE = 10;

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > MAX_SIZE) {
            history.remove(0);

        }

    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
