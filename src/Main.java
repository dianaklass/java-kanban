import tasks.*;
import managers.*;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Manager.getDefault();

        // Создание задач
        Task task1 = new Task("Купить продукты на ужин", "Сделать заказ в Яндекс Еда", Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task("Поехать на день рождения", "Заказать такси в Яндекс Такси", Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(10));
        Epic epic1 = new Epic("Поехать на море", "Подготовить все к поездке", Duration.ofHours(3), LocalDateTime.now().plusDays(1));
        SubTask subTask1 = new SubTask("Купить билеты", "Выбрать компанию перелетов", Duration.ofHours(5), LocalDateTime.now().plusWeeks(1), epic1, Status.NEW);
        SubTask subTask2 = new SubTask("Собрать вещи", "Взять одежду, умывалки", Duration.ofMinutes(120), LocalDateTime.now().plusDays(1).plusMinutes(15), epic1, Status.NEW);
        Epic epic2 = new Epic("Пройти первый модуль", "Закрыть все пять тем", Duration.ofMinutes(90), LocalDateTime.now().plusDays(1).plusMinutes(30));
        SubTask subTask3 = new SubTask("Сдать финальные проекты", "Прочитать теорию", Duration.ofHours(2), LocalDateTime.now().plusWeeks(1).plusMinutes(10), epic2, Status.NEW);

        // Добавление задач
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1); // Добавляем эпик через отдельный метод
        taskManager.addEpic(epic2); // Добавляем эпик через отдельный метод
        taskManager.addSubTask(subTask1); // Добавляем подзадачу
        taskManager.addSubTask(subTask2); // Добавляем подзадачу
        taskManager.addSubTask(subTask3); // Добавляем подзадачу

        // Связывание подзадач с эпиками
        epic1.addSubTask(subTask1);
        epic1.addSubTask(subTask2);
        epic2.addSubTask(subTask3);

        // Проверка получения всех задач, эпиков и подзадач
        System.out.println("Все задачи: " + taskManager.getAllTasks());
        System.out.println("Все эпики: " + taskManager.getAllEpics());
        System.out.println("Все подзадачи: " + taskManager.getAllSubTasks());

        // Проверка получения задачи по ID
        Task task = taskManager.getTaskById(task1.getId());
        System.out.println("Задача по ID (task1): " + task);

        // Проверка удаления задачи по ID
        taskManager.clearById(task1.getId());
        System.out.println("Все задачи после удаления task1: " + taskManager.getAllTasks());

        // Проверка обновления задачи
        Task newTask = new Task("Купить хлеб", "Пойти в пятёрочку", Duration.ofMinutes(10), LocalDateTime.now().plusHours(2));
        newTask.setId(task2.getId());
        taskManager.update(newTask);

        // Вывод всех задач после обновления
        System.out.println("Все задачи после обновления: " + taskManager.getAllTasks());

        // Получение списка подзадач эпика
        System.out.println("Подзадачи эпика 1: " + taskManager.getSubTaskList(epic1.getId()));

        // Обновление статуса эпика (если все подзадачи выполнены, эпик тоже должен стать DONE)
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManager.updateEpicStatus(epic1);
        System.out.println("Статус эпика после обновления: " + epic1.getStatus());

        // Проверка истории просмотров
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(subTask3.getId());
        taskManager.getTaskById(subTask1.getId());

        // Очистка всех задач
        taskManager.clearAllTasks();
        System.out.println("Все задачи после очистки: ");
        System.out.println("Задачи: " + taskManager.getAllTasks());
        System.out.println("Эпики: " + taskManager.getAllEpics());
        System.out.println("Подзадачи: " + taskManager.getAllSubTasks());
    }
}
