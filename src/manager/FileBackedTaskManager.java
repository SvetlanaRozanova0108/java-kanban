import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path path;

    public FileBackedTaskManager(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
        }

        this.path = path;
    }

    public FileBackedTaskManager(String path) throws IOException {
        this(Path.of(path));
    }

    public Path getPath() {
        return path;
    }

    public static FileBackedTaskManager loadFromFile(File file) {

        try {

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file.getPath());
            fileBackedTaskManager.load();
            return fileBackedTaskManager;

        } catch (IOException e) {
            throw ManagerSaveException.loadException(e);
        }
    }

    private void load() throws IOException {
        List<String> lines = Files.readAllLines(this.getPath(), Charset.defaultCharset());
        Integer maxId = 0;

        for (int i = 1; i < lines.size(); i++) {
            var fields = lines.get(i).split(",", 8);

            Integer id = Integer.parseInt(fields[0]);
            Status st = Status.valueOf(fields[3]);
            String name = fields[2];
            String descr = fields[4];
            String type = fields[1];
            LocalDateTime startTime = !fields[5].isEmpty() ? LocalDateTime.parse(fields[5]) : null;
            Duration duration = !fields[6].isEmpty() ? Duration.ofMinutes(Long.parseLong(fields[6])) : null;

            if (maxId < id) {
                maxId = id;
            }

            switch (type) {
                case "task":
                    var t = new Task(id, name, descr, st);
                    tasks.put(t.getId(), t);
                    break;
                case "subtask":
                    int epicId = Integer.parseInt(fields[7]);
                    Epic epic = epics.get(id);
                    var s = new Subtask(id, epicId, name, descr, st);
                    subtasks.put(s.getId(), s);
                    epic.addSubtask(s);
                    break;
                case "epic":
                    var e = new Epic(id, name, descr, st);
                    epics.put(e.getId(), e);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
        }
        super.nextId = maxId;
    }

    // метод сохраняет текущее состояние менеджера в указанный файл
    public void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.getPath().toString()))) {
            bw.write(CSVFormat.getHeader());
            bw.newLine();
            getTasks().stream().forEach(task -> {
                try {
                    bw.write(task.toString());
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            getEpics().stream().forEach(epic -> {
                try {
                    bw.write(epic.toString());
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            getSubtasks().stream().forEach(subtask -> {
                try {
                    bw.write(subtask.toString());
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw ManagerSaveException.saveException(e);
        }

    }

    @Override
    public Task creationTask(Task input) {
        var result = super.creationTask(input);
        save();
        return result;

    }

    @Override
    public Subtask creationSubtask(Subtask input) {
        var result = super.creationSubtask(input);
        save();
        return result;
    }

    @Override
    public Epic creationEpic(Epic input) {
        var result = super.creationEpic(input);
        save();
        return result;
    }

    @Override
    public Task updateTask(Task input) {
        var result = super.updateTask(input);
        save();
        return result;
    }

    @Override
    public Subtask updateSubtask(Subtask input) {
        var result = super.updateSubtask(input);
        save();
        return result;
    }

    @Override
    public Epic updateEpic(Epic input) {
        var result = super.updateEpic(input);
        save();
        return result;
    }

    @Override
    public boolean removeTaskById(Integer id) {
        var result = super.removeTaskById(id);
        save();
        return result;
    }

    @Override
    public boolean removeSubtaskById(Integer id) {
        var result = super.removeSubtaskById(id);
        save();
        return result;
    }

    @Override
    public boolean removeEpicById(Integer id) {
        var result = super.removeEpicById(id);
        save();
        return result;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }
}
