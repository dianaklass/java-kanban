package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager taskManager;
    private static final Gson gson = new Gson();

    public EpicsHandler(TaskManager taskManager) {
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
        if (path.equals("/epics")) {
            List<Epic> epics = taskManager.getAllEpics();
            sendText(exchange, gson.toJson(epics));
        } else {
            sendNotFound(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);

        if (taskManager.getEpicById(epic.getId()) != null) {
            sendHasInteractions(exchange);
            return;
        }

        if (epic.getId() == 0) {
            taskManager.addEpic(epic);
        } else {
            taskManager.update(epic);
        }
        exchange.sendResponseHeaders(201, 0);
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        String[] splitPath = path.split("/");

        if (splitPath.length == 2) {
            taskManager.clearAllEpics();
            exchange.sendResponseHeaders(200, 0);
        } else if (splitPath.length == 3) {
            int epicId = Integer.parseInt(splitPath[2]);

            if (taskManager.getEpicById(epicId) == null) {
                sendNotFound(exchange);
                return;
            }

            taskManager.clearEpicById(epicId);
            exchange.sendResponseHeaders(200, 0);
        } else {
            sendNotFound(exchange);
        }
    }
}

