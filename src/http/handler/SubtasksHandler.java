package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.SubTask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private static final Gson gson = new Gson();

    public SubtasksHandler(TaskManager taskManager) {
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
                exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception e) {
            sendInternalServerError(exchange);
            e.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {
        if (path.equals("/subtasks")) {
            List<SubTask> subTasks = taskManager.getAllSubTasks();
            sendText(exchange, gson.toJson(subTasks));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        SubTask subTask = gson.fromJson(body, SubTask.class);

        List<SubTask> subTasks = taskManager.getSubTaskList(subTask.getEpicId());
        boolean exists = subTasks.stream().anyMatch(st -> st.getId() == subTask.getId());

        if (exists) {
            sendHasInteractions(exchange);
            return;
        }

        if (subTask.getId() == 0) {
            taskManager.addSubTask(subTask);
        } else {
            taskManager.updateSubTaskStatus(subTask, subTask.getStatus());
        }
        exchange.sendResponseHeaders(201, 0);
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] splitPath = path.split("/");

        if (splitPath.length == 2) {
            taskManager.deleteAllSubTasks();
            exchange.sendResponseHeaders(200, 0);
        } else if (splitPath.length == 3) {
            int subTaskId = Integer.parseInt(splitPath[2]);

            List<SubTask> subTasks = taskManager.getSubTaskList(0);
            boolean exists = subTasks.stream().anyMatch(st -> st.getId() == subTaskId);

            if (!exists) {
                sendNotFound(exchange);
                return;
            }

            taskManager.deleteSubTaskById(subTaskId);
            exchange.sendResponseHeaders(200, 0);
        } else {
            sendNotFound(exchange);
        }
    }
}
