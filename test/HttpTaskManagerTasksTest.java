import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;




public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = taskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void handleGetTasksTest() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationTask(task);
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());
        var sut = (List) gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(sut, "Задачи не возвращаются");
        assertEquals(1, sut.size(), "Некорректное количество задач");
        assertEquals("Test 2", sut.getItem(0).getClass().getName(), "Некорректное имя задачи");
    }

    @Test
    public void handleGetTaskByIdTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var sut = gson.fromJson(response.body(), Task.class);
        assertNotNull(sut, "Задачи не возвращаются");
        assertEquals("Test 2", sut.getName(), "Некорректное имя задачи");
    }

    @Test
    public void handlePostTaskUpsertTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List tasksFromManager = (List) manager.getTasks();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", tasksFromManager.getItem(0).getClass().getName(), "Некорректное имя задачи");
    }

    @Test
    public void handleDeleteTaskIdTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void handleGetSubtasksTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдохнуть на море.", "Каспийское море.");
        Epic epicCreated = manager.creationEpic(epic);
        Subtask subtask = new Subtask(epicCreated.getId(),"Test 3", "Testing subtask 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var sut = (List) gson.fromJson(response.body(), new SubtaskListTypeToken().getType());
        assertNotNull(sut, "Подзадачи не возвращаются");
        assertEquals(1, sut.size(), "Некорректное количество подзадач");
        assertEquals("Test 2", sut.getItem(0).getClass().getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void handleGetSubtaskByIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдохнуть на море.", "Каспийское море.");
        Epic epicCreated = manager.creationEpic(epic);
        Subtask subtask = new Subtask(epicCreated.getId(),"Test 3", "Testing subtask 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var sut = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(sut, "Подзадачи не возвращаются");
        assertEquals("Test 2", sut.getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void handlePostSubtaskUpsertTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдохнуть на море.", "Каспийское море.");
        Epic epicCreated = manager.creationEpic(epic);
        Subtask subtask = new Subtask(epicCreated.getId(),"Test 3", "Testing subtask 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subtaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List subtasksFromManager = (List) manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test 2", subtasksFromManager.getItem(0).getClass().getName(), "Некорректное имя задачи");
    }

    @Test
    public void handleDeleteSubtaskIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Отдохнуть на море.", "Каспийское море.");
        Epic epicCreated = manager.creationEpic(epic);
        Subtask subtask = new Subtask(epicCreated.getId(),"Test 3", "Testing subtask 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void handleGetEpicsTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var sut = (List) gson.fromJson(response.body(), new EpicListTypeToken().getType());
        assertNotNull(sut, "Епики не возвращаются");
        assertEquals(1, sut.size(), "Некорректное количество епиков");
        assertEquals("Test 2", sut.getItem(0).getClass().getName(), "Некорректное имя епика");
    }

    @Test
    public void handleGetEpicByIdIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/"+ epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var sut = gson.fromJson(response.body(), Epic.class);
        assertNotNull(sut, "Епики не возвращаются");
        assertEquals("Test 2", sut.getName(), "Некорректное имя епика");
    }

    @Test
    public void handleGetEpicSubtasksIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationEpic(epic);
        Subtask subtask = new Subtask(epic.getId(),"Test 3", "Testing subtask 3",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationSubtask(subtask);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId() + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var sut = gson.fromJson(response.body(), Epic.class);
        assertNotNull(sut, "Епики не возвращаются");
        assertEquals("Test 2", sut.getName(), "Некорректное имя епика");
    }

    @Test
    public void handlePostEpicUpsert() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String epicJson = gson.toJson(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List epicsFromManager = (List) manager.getEpics();
        assertNotNull(epicsFromManager, "Епики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество епиков");
        assertEquals("Test 2", epicsFromManager.getItem(0).getClass().getName(), "Некорректное имя епика");
    }

    @Test
    public void handleDeleteEpicIdTest() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void handleGetHistoryTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var sut = (List) gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(sut, "Задачи не возвращаются");
        assertEquals(1, sut.size(), "Некорректное количество задач");
        assertEquals("Test 2", sut.getItem(0).getClass().getName(), "Некорректное имя задачи");
    }

    @Test
    public void handleGetPrioritizedTasksTest() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2",
                Status.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        manager.creationTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        var sut = (List) gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertNotNull(sut, "Задачи не возвращаются");
        assertEquals(1, sut.size(), "Некорректное количество задач");
        assertEquals("Test 2", sut.getItem(0).getClass().getName(), "Некорректное имя задачи");
    }

}



