package managers;

import tasks.Task;
import tasks.SubTask;
import tasks.Epic;
import tasks.Status;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));

    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile();
    }

    private void loadFromFile() {
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    addEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    addSubTask((SubTask) task);
                } else {
                    addTask(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("type,id,name,description,status,duration,startTime,epicId\n");
            for (Task task : getAllTasks()) writer.write(toString(task) + "\n");
            for (Epic epic : getAllEpics()) writer.write(toString(epic) + "\n");
            for (SubTask subTask : getAllSubTasks()) writer.write(toString(subTask) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String toString(Task task) {
        String type = task instanceof Epic ? "Epic" : (task instanceof SubTask ? "SubTask" : "Task");
        String epicId = task instanceof SubTask ? String.valueOf(((SubTask) task).getEpicId()) : "";
        return String.format("%s,%d,%s,%s,%s,%d,%s,%s",
                type, task.getId(), task.getName(), task.getDescription(), task.getStatus(),
                task.getDuration().toMinutes(),
                task.getStartTime() != null ? task.getStartTime().toString() : "",
                epicId);
    }

    // 🔹 Преобразование строки CSV в объект Task
    private Task fromString(String line) {
        String[] data = line.split(",");
        int id = Integer.parseInt(data[1]);
        String name = data[2];
        String description = data[3];
        Status status = Status.valueOf(data[4]);
        Duration duration = Duration.ofMinutes(Long.parseLong(data[5]));
        LocalDateTime startTime = data[6].isEmpty() ? null : LocalDateTime.parse(data[6]);

        switch (data[0]) {
            case "Epic":
                Epic epic = new Epic(name, description, duration, startTime);
                epic.setId(id);
                return epic;
            case "SubTask":
                int epicId = Integer.parseInt(data[7]);
                // Получаем эпик по ID
                Epic epicForSubTask = findEpicById(epicId);
                // Создаем подзадачу, передавая все необходимые параметры
                SubTask subTask = new SubTask(name, description, duration, startTime, epicForSubTask, status);
                subTask.setId(id);
                return subTask;
            default:
                Task task = new Task(name, description, duration, startTime);
                task.setId(id);
                task.setStatus(status);
                return task;
        }
    }

    private Epic findEpicById(int epicId) {
        for (Epic epic : getAllEpics()) {
            if (epic.getId() == epicId) {
                return epic;
            }
        }
        return null;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        prioritizedTasks.add(task);
        saveToFile();
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public Task getTaskById(int id) {
        return super.getTaskById(id);
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        prioritizedTasks.clear();
        saveToFile();
    }

    @Override
    public void clearById(int id) {
        Task task = getTaskById(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            super.clearById(id);
            saveToFile();
        }
    }

    @Override
    public void update(Task newTask) {
        super.update(newTask);
        prioritizedTasks.remove(newTask);
        prioritizedTasks.add(newTask);
        saveToFile();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        saveToFile();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public Epic getEpicById(int id) {
        return super.getEpicById(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        saveToFile();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        saveToFile();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        prioritizedTasks.add(subTask);
        saveToFile();
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public List<SubTask> getSubTaskList(int epicId) {
        return super.getSubTaskList(epicId);
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = getSubTaskById(id);
        if (subTask != null) {
            prioritizedTasks.remove(subTask);
            super.deleteSubTaskById(id);
            saveToFile();
        }
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        prioritizedTasks.removeIf(task -> task instanceof SubTask);
        saveToFile();
    }

    @Override
    public void updateSubTaskStatus(SubTask subTask, Status newStatus) {
        super.updateSubTaskStatus(subTask, newStatus);
        prioritizedTasks.remove(subTask);
        prioritizedTasks.add(subTask);
        saveToFile();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        saveToFile();
    }

    public void save() {
        saveToFile();
    }

    public boolean checkTaskOverlap(Task task1, Task task2) {
        if (task1.getStartTime().isBefore(task2.getEndTime()) && task1.getEndTime().isAfter(task2.getStartTime())) {
            return true;
        }
        return false;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    public SubTask getSubTaskById(int id) {
        return (SubTask) super.getTaskById(id);
    }
}
