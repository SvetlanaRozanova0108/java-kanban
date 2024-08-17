import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = requestPath.split("/");
        if (pathParts[1].equals("epics")) {
            if (requestMethod.equals("GET")) {
                if (pathParts.length == 2) {
                    handleGetEpics(exchange);
                }
                if (pathParts.length == 3) {
                    handleGetEpicByIdId(exchange);
                } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                    handleGetEpicSubtasksId(exchange);
                }
            }
            if (requestMethod.equals("POST")) {
                handlePostEpicUpsert(exchange);
            }
            if (requestMethod.equals("DELETE")) {
                handleDeleteEpicId(exchange);
            }
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        var epics = taskManager.getEpics();
        var response = gson.toJson(epics);
        sendText(exchange, response);
    }

    private void handleGetEpicByIdId(HttpExchange exchange) throws IOException {
        var epicId = getId(exchange);
        if (epicId.isEmpty()) {
            sendNotFound(exchange, "not number");
        }
        try {
            var epics = taskManager.getEpicById(epicId.get());
            var response = gson.toJson(epics);
            sendText(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange, epicId.get() + "not found");
        }
    }

    private void handleGetEpicSubtasksId(HttpExchange exchange) throws IOException {
        var epicSubtaskId = getId(exchange);
        if (epicSubtaskId.isEmpty()) {
            sendNotFound(exchange, "not number");
        }
        var epics = taskManager.getSubtasksByEpicId(epicSubtaskId.get());
        var response = gson.toJson(epics);
        sendText(exchange, response);
    }

    private void handlePostEpicUpsert(HttpExchange exchange) throws IOException {
        var body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        if (epic.getId() != null) {
            taskManager.updateEpic(epic);
            sendText(exchange, "");
        } else {
            taskManager.creationEpic(epic);
            sendText(exchange, "");
        }
    }

    private void handleDeleteEpicId(HttpExchange exchange) throws IOException {
        var delEpicId = getId(exchange);
        if (delEpicId.isEmpty()) {
            sendNotFound(exchange, "not number");
        }
        var result = taskManager.removeEpicById(delEpicId.get());
        var response = "";
        if (result) {
            sendText(exchange, response);
        } else {
            sendNotFound(exchange, response);
        }
    }
}

