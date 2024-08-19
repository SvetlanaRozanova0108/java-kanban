import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = requestPath.split("/");
        if (pathParts[1].equals("prioritized")) {
            if (requestMethod.equals("GET")) {
                handleGetPrioritizedTasks(exchange);
            }
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        var prioritized = taskManager.getPrioritizedTasks();
        var response = gson.toJson(prioritized);
        sendText(exchange, response);
    }
}

