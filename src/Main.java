import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;
import managers.Manager;
import  managers.TaskManager;


public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Manager.getDefault();

        // Создание задач
        Task task1 = new Task("Купить продукты на ужин", "Сделать заказ в Яндекс Еда");
        Task task2 = new Task("Поехать на день рождения", "Заказать такси в Яндекс Такси");
        Epic epic1 = new Epic("Поехать на море", "Подготовить все к поездке");
        SubTask subTask1 = new SubTask("Купить билеты", "Выбрать компанию перелетов");
        SubTask subTask2 = new SubTask("Собрать вещи", "Взять одежду, умывалки");
        Epic epic2 = new Epic("Пройти первый модуль", "Закрыть все пять тем");
        SubTask subTask3 = new SubTask("Сдать финальные проекты", "Прочитать теорию");

        // Добавление задач
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(epic1);
        taskManager.addTask(epic2);
        taskManager.addTask(subTask1);
        taskManager.addTask(subTask2);
        taskManager.addTask(subTask3);

        // Связывание подзадач с эпиками
        epic1.addSubTask(subTask1);
        epic1.addSubTask(subTask2);
        epic2.addSubTask(subTask3);

        // Проверка получения всех задач, эпиков и подзадач
        taskManager.getAllTasks();
        taskManager.getAllEpics();
        taskManager.getAllSubTasks();

        // Проверка получения задачи по ID
        taskManager.getTaskById(task1.getId());

        // Проверка удаления задачи по ID
        taskManager.clearById(task1.getId());

        // Проверка обновления задачи
        Task newTask = new Task("Купить хлеб", "Пойти в пятёрочку");
        newTask.setId(task2.getId());
        taskManager.update(newTask);

        // Вывод всех задач после обновления
        taskManager.getAllTasks();
        taskManager.getAllEpics();
        taskManager.getAllSubTasks();

        // Получение списка подзадач эпика
        taskManager.getSubTaskList(epic1.getId());

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

        // Проверка, что все задачи, эпики и подзадачи удалены
        taskManager.getAllTasks();
        taskManager.getAllEpics();
        taskManager.getAllSubTasks();
    }
}