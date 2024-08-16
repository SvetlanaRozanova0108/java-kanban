import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;


public class Task {

    private Integer id;
    private String name;
    private String description;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String name, String description) {
        this.status = Status.NEW;
        this.name = name;
        this.description = description;
    }

    public Task(Integer id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, String descr, Status status, Duration duration, LocalDateTime start) {
        this.name = name;
        this.description = descr;
        this.status = status;
        this.duration = duration;
        this.startTime = start;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    //дата и время завершения задачи
    public LocalDateTime getEndTime() {
        if (this.startTime == null) {
            return null;
        }
        if (this.duration == null) {
            return this.startTime;
        }
        return this.startTime.plus(duration);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return Objects.equals(id, task.id) && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        String durStr = "";
        if (duration != null) {
            durStr = String.valueOf(duration.toMinutes());
        }
        var start = getStartTime();
        var startStr = start != null ? String.valueOf(start) : "";
        return id + ",task," + name + "," + status + "," + description + "," + startStr + "," + durStr + ",";
    }
}

