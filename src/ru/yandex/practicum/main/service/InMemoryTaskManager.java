package ru.yandex.practicum.main.service;

import ru.yandex.practicum.main.model.Epic;
import ru.yandex.practicum.main.model.Status;
import ru.yandex.practicum.main.model.SubTask;
import ru.yandex.practicum.main.model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private int idCounter = 1;

    // Методы для работы с задачами
    @Override
    public void addTask(Task task) {
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearById(int id) {
        tasks.remove(id);
    }

    @Override
    public void update(Task newTask) {
        if (tasks.containsKey(newTask.getId())) {
            tasks.put(newTask.getId(), newTask);
        }
    }

    // Методы для работы с эпиками
    @Override
    public void addEpic(Epic epic) {
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
        }
    }

    @Override
    public void clearAllEpics() {
        epics.clear();
    }

    // Методы для работы с подзадачами
    @Override
    public void addSubTask(SubTask subTask) {
        subTask.setId(idCounter++);
        subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getSubTaskList(int epicId) {
        List<SubTask> result = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpic() != null && subTask.getEpic().getId() == epicId) {
                result.add(subTask);
            }
        }
        return result;
    }

    @Override
    public void deleteSubTaskById(int id) {
        subTasks.remove(id);
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    @Override
    public void updateSubTaskStatus(SubTask subTask, Status newStatus) {
        if (subTasks.containsKey(subTask.getId())) {
            subTask.setStatus(newStatus);
        }
    }


    @Override
    public List<Task> getHistory() {

        return new ArrayList<>(tasks.values());
    }


    @Override
    public void updateEpicStatus(Epic epic) {
        boolean allDone = true;
        for (SubTask subTask : getSubTaskList(epic.getId())) {
            if (subTask.getStatus() != Status.DONE) {
                allDone = false;
                break;
            }
        }


        if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    @Override
    public void printError(String message) {
        System.out.println("Ошибка: " + message);
    }
}