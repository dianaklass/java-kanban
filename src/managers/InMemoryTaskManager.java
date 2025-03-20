package managers;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private int idCounter = 1;
    private final HistoryManager historyManager;

    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));

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
        prioritizedTasks.add(task);
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
        prioritizedTasks.clear();
    }

    @Override
    public void clearById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public void update(Task newTask) {
        if (newTask == null) {
            printError("Задачу нельзя обновить, так как она пуста");
            return;
        }
        tasks.put(newTask.getId(), newTask);
        prioritizedTasks.remove(newTask);
        prioritizedTasks.add(newTask);
    }

    // Методы для работы с эпиками
    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            printError("Пустой эпик нельзя добавить");
            return;
        }
        epic.setId(idCounter++);
        epics.put(epic.getId(), epic);
        updateEpicTimeFields(epic);
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
        if (epic == null) {
            printError("Нельзя обновить пустой эпик");
            return;
        }
        if (!epics.containsKey(epic.getId())) {
            printError("Эпик с ID " + epic.getId() + " не найден");
            return;
        }
        epics.put(epic.getId(), epic);
        updateEpicTimeFields(epic);
    }

    @Override
    public void clearAllEpics() {
        epics.clear();
    }

    // Методы для работы с подзадачами
    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            printError("Пустую подзадачу нельзя добавить");
            return;
        }
        subTask.setId(idCounter++);
        subTasks.put(subTask.getId(), subTask);
        prioritizedTasks.add(subTask);
        if (subTask.getEpic() != null) {
            updateEpicTimeFields(subTask.getEpic());
        }
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getSubTaskList(int epicId) {
        List<SubTask> result = new ArrayList<>();
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == epicId) {
                result.add(subTask);
            }
        }
        return result;
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            prioritizedTasks.remove(subTask);
        }
    }

    @Override
    public void deleteAllSubTasks() {
        subTasks.clear();
        prioritizedTasks.removeIf(task -> task instanceof SubTask);
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
        prioritizedTasks.remove(subTask);
        prioritizedTasks.add(subTask);
        updateEpicTimeFields(subTask.getEpic());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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
        prioritizedTasks.remove(epic);
        prioritizedTasks.add(epic);
    }

    private void updateEpicTimeFields(Epic epic) {
        epic.updateEpicData();
        prioritizedTasks.remove(epic);
        prioritizedTasks.add(epic);
    }

    public void printError(String message) {
        System.out.println(message);
    }
}
