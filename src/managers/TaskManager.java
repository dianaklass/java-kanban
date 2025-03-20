package managers;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    // Методы для работы с задачами
    void addTask(Task task);

    List<Task> getAllTasks();

    Task getTaskById(int id);

    void clearAllTasks();

    void clearById(int id);

    void update(Task newTask);

    // Методы для работы с эпиками
    void addEpic(Epic epic);

    List<Epic> getAllEpics();

    Epic getEpicById(int id);

    void updateEpic(Epic epic);

    void clearAllEpics();

    // Методы для работы с подзадачами
    void addSubTask(SubTask subTask);

    List<SubTask> getAllSubTasks();

    List<SubTask> getSubTaskList(int epicId);

    void deleteSubTaskById(int id);

    void deleteAllSubTasks();

    void updateSubTaskStatus(SubTask subTask, Status newStatus);

    List<Task> getHistory();

    void updateEpicStatus(Epic epic);

    // Метод для получения приоритетных задач
    List<Task> getPrioritizedTasks();

    default void printError(String message) {
        System.out.println("Ошибка: " + message);
    }
}
