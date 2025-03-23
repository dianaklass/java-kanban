package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import managers.Manager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

public class

HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private static final Gson gson = new Gson();

    // Конструктор сервера
    public HttpTaskServer() throws IOException {
        this.taskManager = Manager.getDefault();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.setExecutor(Executors.newFixedThreadPool(4));

        Map<String, HttpHandler> handlers = Map.of(
                "/tasks", new TasksHandler(taskManager),
                "/epics", new EpicsHandler(taskManager),
                "/subtasks", new SubtasksHandler(taskManager),
                "/history", new HistoryHandler(taskManager)
        );

        for (Map.Entry<String, HttpHandler> entry : handlers.entrySet()) {
            server.createContext(entry.getKey(), entry.getValue());
        }
    }

    public void start() {
        try {
            server.start(); // Запускаем сервер
            System.out.println("Сервер запущен на порту " + PORT); // Выводим сообщение о запуске
        } catch (Exception e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage()); // Ошибка при запуске
            e.printStackTrace();
        }
    }

    public void stop() {
        server.stop(0); // Останавливаем сервер
        System.out.println("Сервер остановлен."); // Выводим сообщение о остановке
    }

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer(); // Создаем сервер
            server.start();
        } catch (Exception e) {
            System.err.println("Ошибка при создании сервера: " + e.getMessage()); // Ошибка при создании сервера
            e.printStackTrace();
        }
    }
}
