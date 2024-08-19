import java.util.List;
import java.util.Set;

public interface TaskManager {

    int getNextId();


    Task creationTask(Task input);

    Subtask creationSubtask(Subtask input);

    Epic creationEpic(Epic input);

    Task getTaskById(Integer id);

    Subtask getSubtaskById(Integer id);

    Epic getEpicById(Integer id);

    Task updateTask(Task input);

    Subtask updateSubtask(Subtask input);

    Epic updateEpic(Epic input);

    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();

    List<Subtask> getSubtasksByEpicId(Integer id);

    boolean removeTaskById(Integer id);

    boolean removeSubtaskById(Integer id);

    boolean removeEpicById(Integer id);

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
