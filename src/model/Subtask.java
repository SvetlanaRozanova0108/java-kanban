import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private Integer epicId;

    public Subtask(Integer epicId, String name, String description) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(Integer id, Integer epicId, String name, String description, Status status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Subtask(int epicId, String name, String descr, Status status, Duration duration, LocalDateTime start) {
        super(name, descr, status, duration, start);
        this.epicId = epicId;
    }

    public int getEpicID() {
        return epicId;
    }

    @Override
    public String toString() {
        var dur = getDuration();
        var durStr = dur != null ? String.valueOf(dur.toMinutes()) : "";
        var start = getStartTime();
        var startStr = start != null ? String.valueOf(start) : "";
        return getId() + ",subtask," + getName() + "," + getStatus() + "," + getDescription() + "," + startStr + "," + durStr + "," + epicId;
    }
}
