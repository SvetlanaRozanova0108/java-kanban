import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class Epic extends Task {


    private LocalDateTime epicEndTime;

    private final ArrayList<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Epic(String name, String description, Status status, Duration duration, LocalDateTime start) {
        super(name, description, status, duration, start);
    }

    public void addSubtask(Subtask input) {
        subtaskIds.add(input.getId());
    }

    public void removeSubtask(Subtask input) {
        subtaskIds.remove(input.getId());
    }

    public Collection<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return epicEndTime;
    }

    public void setEndTime(LocalDateTime epicEndTime) {
        this.epicEndTime = epicEndTime;
    }

    @Override
    public String toString() {
        var dur = getDuration();
        var durStr = dur != null ? String.valueOf(dur.toMinutes()) : "";
        var start = getStartTime();
        var startStr = start != null ? String.valueOf(start) : "";
        return getId() + ",epic," + getName() + "," + getStatus() + "," + getDescription() + "," + startStr + "," + durStr + ",";
    }
}
