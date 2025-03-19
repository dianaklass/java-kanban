package managers;

import tasks.Epic;
import tasks.Task;
import tasks.SubTask;
import java.util.*;
import tasks.Status;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private int idCounter = 1;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.historyManager = Manager.getDefaultHistory();
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            printError("Пустую задачу нельзя добавить");
            return;
        }
        task.setId(idCounter++);
        tasks.put(task.getId(), task);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        } else {
            printError("Задача с ID " + id + " не найдена");
        }
        return task;
    }

    @Override
    public void clearAllTasks() {
        tasks.clear();
    }

    @Override
    public void clearById(int id) {
        if (!tasks.containsKey(id)) {
            printError("Невозможно удалить: задача с ID " + id + " не найдена");
            return;
        }
        tasks.remove(id);
    }

    @Override
    public void update(Task newTask) {
        if (newTask == null) {
            printError("Нельзя обновить пустую задачу");
            return;
        }
        if (!tasks.containsKey(newTask.getId())) {
            printError("Задача с ID " + newTask.getId() + " не найдена");
            return;
        }
        tasks.put(newTask.getId(), newTask);
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            printError("Пустой эпик нельзя добавить");
            return;
        }
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            printError("Эпик с ID " + id + " не найден");
        }
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            printError("Нельзя обновить пустой эпик");
            return;
        }
        if (!epics.containsKey(epic.getId())) {
            printError("Эпик с ID " + epic.getId() + " не найден");
            return;
        }
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
    }

    @Override
    public void clearAllEpics() {
        epics.clear();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            printError("Пустую подзадачу нельзя добавить");
            return;
        }
        subTask.setId(idCounter++);
        subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getSubTaskList(int epicId) {
        if (!epics.containsKey(epicId)) {
            printError("Эпик с ID " + epicId + " не найден");
            return new ArrayList<>();
        }
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
        if (!subTasks.containsKey(id)) {
            printError("Подзадача с ID " + id + " не найдена");
            return;
        }
        subTasks.remove(id);
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
    }

    @Override
    public void updateSubTaskStatus(SubTask subTask, Status newStatus) {
        if (subTask == null) {
            printError("Нельзя обновить статус пустой подзадачи");
            return;
        }
        if (!subTasks.containsKey(subTask.getId())) {
            printError("Подзадача с ID " + subTask.getId() + " не найдена");
            return;
        }
        subTask.setStatus(newStatus);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        if (epic == null) {
            printError("Нельзя обновить статус пустого эпика");
            return;
        }
        if (!epics.containsKey(epic.getId())) {
            printError("Эпик с ID " + epic.getId() + " не найден");
            return;
        }

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

