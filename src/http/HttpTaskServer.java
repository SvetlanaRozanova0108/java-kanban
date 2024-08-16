import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private HttpServer httpServer;
    private final TaskManager taskManager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = new Gson();
    }

    public static void main(String[] args) throws IOException {
        File saveTmpFile = File.createTempFile("data", null);
        var taskManager = FileBackedTaskManager.loadFromFile(saveTmpFile);
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.start();

        //taskServer.stop();
    }

    public void stop() {
        httpServer.stop(1);
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(8080), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new TasksHandler(taskManager, gson));
        httpServer.createContext("/epics", new TasksHandler(taskManager, gson));
        httpServer.createContext("/history", new TasksHandler(taskManager, gson));
        httpServer.createContext("/prioritized", new TasksHandler(taskManager, gson));
        httpServer.start();
    }

    public Gson getGson() {
        return gson;
    }
}
