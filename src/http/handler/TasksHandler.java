package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private static final Gson gson = new Gson();

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET")) {
                handleGet(exchange, path);
            } else if (method.equals("POST")) {
                handlePost(exchange);
            } else if (method.equals("DELETE")) {
                handleDelete(exchange, path);
            } else {
                exchange.sendResponseHeaders(405, 0); // Метод не поддерживается
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);

            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/tasks")) {
            List<Task> tasks = taskManager.getAllTasks();
            sendText(exchange, gson.toJson(tasks));
        } else if (path.startsWith("/tasks/status/")) {
            String[] splitPath = path.split("/");
            if (splitPath.length < 3) {
                sendNotFound(exchange);
                return;
            }
            String statusString = splitPath[3];
            try {
                Status status = Status.valueOf(statusString.toUpperCase());
                List<Task> tasksByStatus = taskManager.getTasksByStatus(status);
                sendText(exchange, gson.toJson(tasksByStatus));
            } catch (IllegalArgumentException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);

        if (taskManager.getTaskById(task.getId()) != null) {
            sendHasInteractions(exchange);
            return;
        }

        if (task.getId() == 0) {
            taskManager.addTask(task);
        } else {
            taskManager.update(task);
        }
        exchange.sendResponseHeaders(201, 0);
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] splitPath = path.split("/");

        if (splitPath.length == 2) {
            taskManager.clearAllTasks();
            exchange.sendResponseHeaders(200, 0);
        } else if (splitPath.length == 3) {
            int taskId = Integer.parseInt(splitPath[2]);

            if (taskManager.getTaskById(taskId) == null) {
                sendNotFound(exchange);
                return;
            }

            taskManager.clearTaskById(taskId);
            exchange.sendResponseHeaders(200, 0);
        } else {
            sendNotFound(exchange);
        }
    }
}


