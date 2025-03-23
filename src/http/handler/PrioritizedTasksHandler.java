package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedTasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private static final Gson gson = new Gson();

    public PrioritizedTasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("GET")) {
                handleGet(exchange, path);
            } else {
                exchange.sendResponseHeaders(405, 0);
            }
        } finally {
            exchange.close();
        }
    }

    // В PrioritizedTasksHandler
    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/tasks/prioritized")) {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
            sendText(exchange, gson.toJson(prioritizedTasks));
        } else {
            sendNotFound(exchange);
        }
    }


}
