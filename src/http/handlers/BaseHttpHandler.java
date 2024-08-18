import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class BaseHttpHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws RuntimeException {
        try (OutputStream os = exchange.getResponseBody(); exchange) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ;
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        String requestPath = exchange.getRequestURI().getPath();
        String[] pathParts = requestPath.split("/");
        int result;
        try {
            result = Integer.parseInt(pathParts[2]);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    protected void sendText(HttpExchange h, String text) throws RuntimeException {
        try (h) {
            byte[] resp = text.getBytes(StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            h.sendResponseHeaders(resp.length == 0 ? 201 : 200, resp.length);
            h.getResponseBody().write(resp);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //sendNotFound — для отправки ответа в случае, если объект не был найден
    public void sendNotFound(HttpExchange exchange, String response) throws IOException {
        writeResponse(exchange, response, 404);
    }

    //sendHasInteractions — для отправки ответа, если при создании или обновлении задача пересекается с уже существующими
    public void sendHasInteractions(HttpExchange exchange, String response) throws IOException {
        writeResponse(exchange, response, 406);
    }
}
