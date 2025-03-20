package managers;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import exeptions.ManagerSaveException;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(task.toString());
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString());
                writer.newLine();
            }
            for (SubTask subTask : getAllSubTasks()) {
                writer.write(subTask.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine(); // Пропускаем заголовок
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[0].equals("Task")) {
                    Task task = new Task(data[2], data[3]);
                    task.setId(Integer.parseInt(data[1]));
                    try {
                        task.setStatus(Status.valueOf(data[4]));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Статус не найден для задачи с ID: " + data[1]);
                        task.setStatus(Status.NEW);
                    }
                    manager.addTask(task);
                } else if (data[0].equals("Epic")) {
                    Epic epic = new Epic(data[2], data[3]);
                    epic.setId(Integer.parseInt(data[1]));
                    try {
                        epic.setStatus(Status.valueOf(data[4]));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Статус не найден для эпика с ID: " + data[1]);
                        epic.setStatus(Status.NEW);
                    }
                    manager.addEpic(epic);
                } else if (data[0].equals("SubTask")) {
                    SubTask subTask = new SubTask(data[2], data[3]);
                    subTask.setId(Integer.parseInt(data[1]));
                    try {
                        subTask.setStatus(Status.valueOf(data[4]));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Статус не найден для подзадачи с ID: " + data[1]);
                        subTask.setStatus(Status.NEW);
                    }
                    manager.addSubTask(subTask);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return manager;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void update(Task newTask) {
        super.update(newTask);
        save();
    }

    @Override
    public void clearById(int id) {
        super.clearById(id);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void updateSubTaskStatus(SubTask subTask, Status newStatus) {
        super.updateSubTaskStatus(subTask, newStatus);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }
}
