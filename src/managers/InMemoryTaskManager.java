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

    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));

    public InMemoryTaskManager() {
        this.historyManager = new InMemoryHistoryManager();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }


    public boolean checkTaskOverlap(Task task1, Task task2) {
        if (task1.getStartTime().isBefore(task2.getEndTime()) && task1.getEndTime().isAfter(task2.getStartTime())) {
            return true;
        }

        return false;
    }

    @Override
    public List<Task> getTasksByStatus(Status status) {
        List<Task> tasksByStatus = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getStatus() == status) {
                tasksByStatus.add(task);
            }
        }
        return tasksByStatus;
    }

    @Override
    public void clearTaskById(int id) {
        Task task = tasks.remove(id);  // Удаляем задачу по её ID
        if (task != null) {
            prioritizedTasks.remove(task);  // Убираем её из приоритетных задач
        } else {
            printError("Задача с ID " + id + " не найдена");
        }
    }

    private boolean isTaskTimeValid(Task task) {
        for (Task existingTask : prioritizedTasks) {
            if (checkTaskOverlap(task, existingTask)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            printError("Пустую задачу нельзя добавить");
            return;
        }

        if (!isTaskTimeValid(task)) {
            printError("Задача перекрывается по времени с другой задачей");
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
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
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
        if (!isTaskTimeValid(newTask)) {
            printError("Задача перекрывается по времени с другой задачей");
            return;
        }
        tasks.put(newTask.getId(), newTask);
        prioritizedTasks.remove(newTask);
        prioritizedTasks.add(newTask);
    }


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

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            printError("Пустую подзадачу нельзя добавить");
            return;
        }
        if (!isTaskTimeValid(subTask)) {
            printError("Подзадача перекрывается по времени с другой задачей");
            return;
        }
        subTask.setId(idCounter++);
        subTasks.put(subTask.getId(), subTask);
        prioritizedTasks.add(subTask);

        if (subTask.getEpic() != null) {
            Epic epic = subTask.getEpic();
            epic.addSubTask(subTask);
            updateEpicStatus(epic);
        }
    }


    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<SubTask> getSubTaskList(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubTasks();
        } else {
            printError("Эпик с ID " + epicId + " не найден");
            return Collections.emptyList();
        }
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            prioritizedTasks.remove(subTask);
            Epic epic = subTask.getEpic();
            if (epic != null) {
                epic.removeSubTask(subTask);
            }
        } else {
            printError("Подзадача с ID " + id + " не найдена");
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
            printError("Подзадача не может быть обновлена, так как она пуста");
            return;
        }
        subTask.setStatus(newStatus);
        if (subTask.getEpic() != null) {
            updateEpicStatus(subTask.getEpic());
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        boolean hasNew = false;
        boolean hasDone = true;
        boolean hasInProgress = false;

        for (SubTask subTask : epic.getSubTasks()) {
            if (subTask.getStatus() == Status.NEW) {
                hasNew = true;
            }
            if (subTask.getStatus() == Status.IN_PROGRESS) {
                hasInProgress = true;
            }
            if (subTask.getStatus() != Status.DONE) {
                hasDone = false;
            }
        }
        if (hasInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else if (hasDone && !hasNew) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void clearEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {

            for (SubTask subTask : epic.getSubTasks()) {
                subTasks.remove(subTask.getId());
                prioritizedTasks.remove(subTask);
            }
            prioritizedTasks.remove(epic);
        } else {
            printError("Эпик с ID " + id + " не найден");
        }
    }


    private void updateEpicTimeFields(Epic epic) {

    }

    public void printError(String message) {
        System.out.println("Ошибка: " + message);
    }
}
