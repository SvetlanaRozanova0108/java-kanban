import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = requestPath.split("/");
        if (pathParts[1].equals("tasks")) {
            if (requestMethod.equals("GET")) {
                if (pathParts.length == 2) {
                    handleGetTasks(exchange);
                }
                handleGetTaskById(exchange);
            }
            if (requestMethod.equals("POST")) {
                handlePostTaskUpsert(exchange);
            }
            if (requestMethod.equals("DELETE")) {
                handleDeleteTaskId(exchange);
            }
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        var tasks = taskManager.getTasks();
        var response = gson.toJson(tasks);
        sendText(exchange, response);
    }

    private void handleGetTaskById(HttpExchange exchange) throws IOException {
        var taskId = getId(exchange);
        if (taskId.isEmpty()) {
            sendNotFound(exchange, "not number");
        }
        try {
            var tasks = taskManager.getTaskById(taskId.get());
            var response = gson.toJson(tasks);
            sendText(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange, taskId.get() + "not found");
        }
    }

    private void handlePostTaskUpsert(HttpExchange exchange) throws IOException {
        var body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        Task result = null;
        if (task.getId() != null) {

            result = taskManager.updateTask(task);

        } else {
            result = taskManager.creationTask(task);

        }
        var response = gson.toJson(result);
        sendText(exchange, response);
    }

    private void handleDeleteTaskId(HttpExchange exchange) throws IOException {
        var delTaskId = getId(exchange);
        if (delTaskId.isEmpty()) {
            sendNotFound(exchange, "not number");
        }
        var result = taskManager.removeTaskById(delTaskId.get());
        var response = "";
        if (result) {
            sendText(exchange, response);
        } else {
            sendNotFound(exchange, response);
        }
    }
}
