import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
        String requestPath = exchange.getRequestURI().getPath();
        String requestMethod = exchange.getRequestMethod();
        String[] pathParts = requestPath.split("/");
        if (pathParts[1].equals("history")) {
            if (requestMethod.equals("GET")) {
                handleGetHistory(exchange);
            }
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        var history = taskManager.getHistory();
        var response = gson.toJson(history);
        sendText(exchange, response);
    }
}
