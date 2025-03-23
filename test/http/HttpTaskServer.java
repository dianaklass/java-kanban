/* package http;

import http.handler.HttpTaskServer;

import com.google.gson.Gson;
import managers.Manager;
import managers.TaskManager;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.http.HttpClient;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private static HttpTaskServer server;
    private static final Gson gson = new Gson();
    private static HttpClient client;
    private static TaskManager taskManager;

    @BeforeAll
    static void startServer() throws IOException {
        taskManager = Manager.getDefault();
        server = new HttpTaskServer();
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearData() {
        taskManager.clearAllTasks();
        taskManager.clearAllEpics();
        taskManager.deleteAllSubTasks();
    }
} */

