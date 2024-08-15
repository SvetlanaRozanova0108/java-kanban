import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtasksHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = requestPath.split("/");
        if (pathParts[1].equals("subtasks")) {
            if (requestMethod.equals("GET")) {
                if (pathParts.length == 2) {
                    handleGetSubtasks(exchange);
                }
                handleGetSubtaskById(exchange);
            }
            if (requestMethod.equals("POST")) {
                handlePostSubtaskUpsert(exchange);
            }
            if (requestMethod.equals("DELETE")) {
                handleDeleteSubtaskId(exchange);
            }
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        var subtasks = taskManager.getSubtasks();
        var response = gson.toJson(subtasks);
        sendText(exchange, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        var subtaskId = getId(exchange);
        if (subtaskId.isEmpty()) {
            sendNotFound(exchange, "not number");
        }
        try {
            var subtasks = taskManager.getSubtaskById(subtaskId.get());
            var response = gson.toJson(subtasks);
            sendText(exchange, response);
        } catch (NotFoundException e) {
            sendNotFound(exchange, subtaskId.get() + "not found");
        }
    }

    private void handlePostSubtaskUpsert(HttpExchange exchange) throws IOException {
        var body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        if(subtask.getId() > 0) {
            taskManager.updateSubtask(subtask);
            sendText(exchange, "");
        } else {
            taskManager.creationSubtask(subtask);
            sendText(exchange, "");
        }
    }

    private void handleDeleteSubtaskId(HttpExchange exchange) throws IOException {
        var delSubtaskId = getId(exchange);
        if(delSubtaskId.isEmpty()){
            sendNotFound(exchange, "not number");
        }
        var result = taskManager.removeSubtaskById(delSubtaskId.get());
        var response = "";
        if (result) {
            sendText(exchange, response);
        } else {
            sendNotFound(exchange, response);
        }
    }
}