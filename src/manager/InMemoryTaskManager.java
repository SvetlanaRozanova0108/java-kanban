import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {


    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(new PrioritizedTasksComparator());

    protected int nextId = 1;

    //Генератор Id
    public int getNextId() {
        return nextId++;
    }

    //Получение списка всех задач
    @Override
    public Collection<Task> getTasks() {
        Collection<Task> result =  tasks.values();
        return result;
    }

    //Получение списка всех подзадач
    @Override
    public Collection<Subtask> getSubtasks() {
        Collection<Subtask> result =  subtasks.values();
        return result;
    }

    //Получение списка всех эпиков
    @Override
    public Collection<Epic> getEpics() {
        Collection<Epic> result =  epics.values();
        return result;
    }

    //Получение списка всех подзадач определённого эпика
    @Override
    public Collection<Subtask> getSubtasksByEpicId(Integer id) {
        Epic currentEpic = getEpicById(id);
        List<Subtask> result = currentEpic.getSubtaskIds().stream()
                .map(subtaskId -> {
                    return subtasks.get(subtaskId);
                })
                .collect(Collectors.toList());
        return result;
    }

    //Удаление всех задач
    @Override
    public void clearTasks() {
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            removePrioritizedTasks(task);
        });
        tasks.clear();
    }

    //Удаление всех подзадач
    @Override
    public void clearSubtasks() {
        subtasks.values().stream().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            removePrioritizedTasks(subtask);
        });

        epics.values().stream().forEach(epic -> {
            epic.clearSubtaskIds();
            epic.setStatus(Status.NEW);
        });
            subtasks.clear();
    }

    //Удаление всех эпиков
    @Override
    public void clearEpics() {
        epics.values().stream().forEach(epic -> {
            historyManager.remove(epic.getId());
        });
        subtasks.clear();
        epics.clear();
    }

    //Получение задач по идентификатору
    @Override
    public Task getTaskById(Integer id) {
        Task result = tasks.get(id);
        historyManager.add(result);
        return result;
    }

    //Получение подзадач по идентификатору
    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask result = subtasks.get(id);
        historyManager.add(result);
        return result;
    }

    //Получение эпиков по идентификатору
    @Override
    public Epic getEpicById(Integer id) {
        Epic result = epics.get(id);
        historyManager.add(result);
        return result;
    }

    //Создание задачи
    @Override
    public Task creationTask(Task input) {
        Integer newId = getNextId();
        input.setId(newId);
        addPrioritizedTasks(input);
        tasks.put(input.getId(), input);
        historyManager.add(input);
        return input;
    }

    //Создание подзадачи
    //Перед созданием подзадачи, необходимо проверить, существует ли её эпик
    @Override
    public Subtask creationSubtask(Subtask input) {
        Integer epicId = input.getEpicID();
        Epic epic = getEpicById(epicId);
        if (epic == null) {
            return null;
        }
        Integer newId = getNextId();
        input.setId(newId);
        addPrioritizedTasks(input);
        subtasks.put(input.getId(), input);
        historyManager.add(input);
        epic.addSubtask(input);
        reCalcAndSaveEpicStatus(epicId);
        reCalcAndSaveEpicTimes(epicId);
        return input;
    }

    //Создание эпика
    @Override
    public Epic creationEpic(Epic input) {
        Integer newId = getNextId();
        input.setId(newId);
        epics.put(input.getId(), input);
        historyManager.add(input);
        return input;
    }

    //Обновление задачи. Новая версия объекта с верным идентификатором передаётся в виде параметра
    @Override
    public Task updateTask(Task input) {
        Task existedTask = getTaskById(input.getId());
        if (existedTask == null) {
            return null;
        }
        addPrioritizedTasks(input);
        tasks.put(input.getId(), input);
        return input;
    }

    //Обновление подзадачи. Новая версия объекта с верным идентификатором передаётся в виде параметра
    @Override
    public Subtask updateSubtask(Subtask input) {
        Subtask existedSubtask = getSubtaskById(input.getId());
        if (existedSubtask == null) {
            return null;
        }
        Integer epicId = input.getEpicID();
        addPrioritizedTasks(input);
        subtasks.put(input.getId(), input);
        reCalcAndSaveEpicStatus(epicId);
        reCalcAndSaveEpicTimes(epicId);
        return input;
    }



    //Обновление эпика. Новая версия объекта с верным идентификатором передаётся в виде параметра
    @Override
    public Epic updateEpic(Epic input) {
        Epic existedEpic = epics.get(input.getId());
        if (existedEpic == null) {
            return null;
        }
        existedEpic.setName(input.getName());
        existedEpic.setDescription(input.getDescription());
        return existedEpic;
    }

    //Удаление задачи по идентификатору
    @Override
    public boolean removeTaskById(Integer id) {
        historyManager.remove(id);
        var rt = tasks.remove(id);
        removePrioritizedTasks(rt);
        return rt != null;
    }

    //Удаление подзадачи по идентификатору
    @Override
    public boolean removeSubtaskById(Integer id) {
        // получить подзадачу по ид
        Subtask subtask = getSubtaskById(id);
        // из нее получаем ид эпика
        Integer epicId = subtask.getEpicID();
        Epic epic = getEpicById(epicId);
        epic.removeSubtask(subtask);
        boolean result = subtasks.remove(id) != null;
        reCalcAndSaveEpicStatus(epicId);
        reCalcAndSaveEpicTimes(epicId);
        historyManager.remove(id);
        removePrioritizedTasks(subtask);
        return result;
    }

    //Удаление эпика по идентификатору
    @Override
    public boolean removeEpicById(Integer id) {
        Collection<Subtask> subtaskCollection = getSubtasksByEpicId(id);
        subtaskCollection.forEach(subtask -> {
            subtasks.remove(subtask.getId());
        });
        historyManager.remove(id);
        return epics.remove(id) != null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Получение статуса эпика
    public Status getStatusEpic(Integer id) {
        Collection<Subtask> subtaskCollection = getSubtasksByEpicId(id);
        Status result = Status.NEW;
        var subTaskInProgress = subtaskCollection
                .stream()
                .filter(f -> f.getStatus() == Status.IN_PROGRESS)
                .collect(Collectors.toList());
        if (!subTaskInProgress.isEmpty()) {
            result = Status.IN_PROGRESS;
        }
        if (result == Status.NEW && allSubtasksByEpicIdIsDone(id)) {
            result = Status.DONE;
        }
        return result;
    }

    private boolean allSubtasksByEpicIdIsDone(int epicID) {
        Collection<Subtask> subtaskCollection = getSubtasksByEpicId(epicID);
        var subTaskInProgress = subtaskCollection
                .stream()
                .filter(subtask -> subtask.getStatus() != Status.DONE)
                .collect(Collectors.toList());
        return subTaskInProgress.isEmpty();
    }

    private void reCalcAndSaveEpicStatus(Integer epicId) {
        Epic epic = getEpicById(epicId);
        Status statusEpic = getStatusEpic(epicId);
        epic.setStatus(statusEpic);
    }

    private void reCalcAndSaveEpicTimes(Integer epicId) {
        Epic epic = getEpicById(epicId);
        LocalDateTime start = getStartTimeEpic(epicId);
        epic.setStartTime(start);
        Duration d = getDurationEpic(epicId);
        epic.setDuration(d);
        LocalDateTime end = getEndTimeEpic(epicId);
        epic.setEndTime(end);
    }

    private LocalDateTime getEndTimeEpic(Integer epicId) {
        var subtasks = getSubtasksByEpicId(epicId);
        final ZoneOffset zone = ZoneOffset.of("Z");
        var resLong = subtasks
                .stream()
                .filter(f -> f.getStartTime() != null)
                .mapToLong(s ->  s.getEndTime().toEpochSecond(zone))
                .reduce(Long::max);
        if (resLong.isPresent()) {
            return LocalDateTime.ofEpochSecond(resLong.getAsLong(), 0, zone);
        }

        return null;
    }

    private Duration getDurationEpic(Integer epicId) {
        var subtasks = getSubtasksByEpicId(epicId);
        long result = subtasks
                .stream()
                .filter(f -> f.getDuration() != null)
                .mapToLong(s -> s.getDuration().toMinutes())
                .sum();
        return Duration.ofMinutes(result);
    }

    private LocalDateTime getStartTimeEpic(Integer epicId) {
        final ZoneOffset zone = ZoneOffset.of("Z");
        var subtasks = getSubtasksByEpicId(epicId);
        var startTime = subtasks
                        .stream()
                        .filter(f -> f.getStartTime() != null)
                        .mapToLong(s ->  s.getStartTime().toEpochSecond(zone))
                        .reduce(Long::min);
        if (!startTime.isEmpty()) {
            LocalDateTime result = LocalDateTime.ofEpochSecond(startTime.getAsLong(), 0, zone);
            return result;
        }
        return null;
    }


    public Set<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private void addPrioritizedTasks(Task input) {
        if (input.getStartTime() == null) {
            return;
        }
        if (!checkingOverlapOfTasks(input)) {
            throw new UnsupportedOperationException("Задача пересекается по времени исполнения с другими задачами.");
        }
        prioritizedTasks.remove(input);
        prioritizedTasks.add(input);
    }

    private void removePrioritizedTasks(Task input) {
        prioritizedTasks.remove(input);
    }

    private boolean checkingOverlapOfTasks(Task input) {
        if (getPrioritizedTasks().isEmpty()) {
            return true;
        }
        var subTaskInProgress = getPrioritizedTasks()
                .stream()
                .filter(task -> !(input.getEndTime().isBefore(task.getStartTime()) || input.getStartTime().isAfter(task.getEndTime())))
                .collect(Collectors.toList());
        return subTaskInProgress.isEmpty();
    }
}


