package managers;

import tasks.Task;
import tasks.SubTask;
import tasks.Epic;
import tasks.Status;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file; // Файл для сохранения данных

    // Конструктор
    public FileBackedTaskManager(File file) {
        this.file = file;
        loadFromFile(); // Загружаем данные из файла
    }

    // Метод для загрузки данных из файла
    private void loadFromFile() {
        if (!file.exists()) return; // Если файл не существует, ничего не делаем
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = fromString(line); // Преобразуем строку в задачу
                if (task instanceof Epic) {
                    super.addEpic((Epic) task); // Добавляем эпик
                } else if (task instanceof SubTask) {
                    super.addSubTask((SubTask) task); // Добавляем подзадачу
                } else {
                    super.addTask(task); // Добавляем задачу
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Ошибка при чтении из файла
        }
    }

    // Метод для сохранения данных в файл
    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("type,id,name,description,status,duration,startTime,epicId\n"); // Записываем заголовок
            for (Task task : getAllTasks()) writer.write(task.toCsvString() + "\n"); // Записываем все задачи
            for (Epic epic : getAllEpics()) writer.write(epic.toCsvString() + "\n"); // Записываем все эпики
            for (SubTask subTask : getAllSubTasks()) writer.write(subTask.toCsvString() + "\n"); // Записываем все подзадачи
        } catch (IOException e) {
            e.printStackTrace(); // Ошибка при записи в файл
        }
    }

    // Метод для преобразования строки в задачу
    private Task fromString(String line) {
        String[] data = line.split(","); // Разделяем строку на части
        if (data.length < 7) {
            // Логируем или выбрасываем ошибку
            System.out.println("Некорректная строка: " + line);
            return null; // Возвращаем null, если строка невалидна
        }

        try {
            int id = Integer.parseInt(data[1]); // Получаем ID
            String name = data[2]; // Имя задачи
            String description = data[3]; // Описание задачи
            Status status = Status.valueOf(data[4]); // Статус задачи

            // Если продолжительность пустая, устанавливаем значение по умолчанию
            Duration duration = data[5].isEmpty() ? Duration.ZERO : Duration.ofMinutes(Long.parseLong(data[5]));

            // Если время начала пустое, устанавливаем значение по умолчанию (например, текущую дату)
            LocalDateTime startTime = data[6].isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(data[6]);

            switch (data[0]) {
                case "Epic":
                    Epic epic = new Epic(name, description, duration, startTime);
                    epic.setId(id);
                    return epic; // Возвращаем эпик
                case "SubTask":
                    int epicId = Integer.parseInt(data[7]);  // Важно, чтобы этот индекс существовал
                    Epic epicForSubTask = findEpicById(epicId); // Находим эпик для подзадачи
                    if (epicForSubTask == null) {
                        System.out.println("Эпик с ID " + epicId + " не найден");
                        return null;
                    }
                    SubTask subTask = new SubTask(name, description, duration, startTime, epicForSubTask, status);
                    subTask.setId(id);
                    return subTask; // Возвращаем подзадачу
                default:
                    Task task = new Task(name, description, duration, startTime);
                    task.setId(id);
                    task.setStatus(status);
                    return task; // Возвращаем обычную задачу
            }
        } catch (Exception e) {
            System.out.println("Ошибка при разборе строки: " + line);
            return null; // Ошибка при разборе строки
        }
    }

    // Метод для поиска эпика по ID
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
        super.addTask(task); // Добавляем задачу
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks(); // Очищаем все задачи
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void clearById(int id) {
        super.clearById(id); // Очищаем задачу по ID
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void update(Task newTask) {
        super.update(newTask); // Обновляем задачу
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic); // Добавляем эпик
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic); // Обновляем эпик
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics(); // Очищаем все эпики
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask); // Добавляем подзадачу
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id); // Удаляем подзадачу по ID
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks(); // Удаляем все подзадачи
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void updateSubTaskStatus(SubTask subTask, Status newStatus) {
        super.updateSubTaskStatus(subTask, newStatus); // Обновляем статус подзадачи
        saveToFile(); // Сохраняем в файл
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic); // Обновляем статус эпика
        saveToFile(); // Сохраняем в файл
    }

    public void save() {
        saveToFile(); // Сохраняем данные в файл
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file); // Загружаем данные из файла
    }
}
