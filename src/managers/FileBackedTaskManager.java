package managers;

import tasks.Task;
import tasks.SubTask;
import tasks.Epic;
import tasks.Status;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile();
    }

    private void loadFromFile() {
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    super.addEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    super.addSubTask((SubTask) task);
                } else {
                    super.addTask(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("type,id,name,description,status,duration,startTime,epicId\n");
            for (Task task : getAllTasks()) writer.write(task.toCsvString() + "\n");
            for (Epic epic : getAllEpics()) writer.write(epic.toCsvString() + "\n");
            for (SubTask subTask : getAllSubTasks()) writer.write(subTask.toCsvString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                Epic epicForSubTask = findEpicById(epicId);
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
        saveToFile();
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        saveToFile();
    }

    @Override
    public void clearById(int id) {
        super.clearById(id);
        saveToFile();
    }

    @Override
    public void update(Task newTask) {
        super.update(newTask);
        saveToFile();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        saveToFile();
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
        saveToFile();
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        saveToFile();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        saveToFile();
    }

    @Override
    public void updateSubTaskStatus(SubTask subTask, Status newStatus) {
        super.updateSubTaskStatus(subTask, newStatus);
        saveToFile();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        saveToFile();
    }

    public void save() {
        saveToFile();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }
}
