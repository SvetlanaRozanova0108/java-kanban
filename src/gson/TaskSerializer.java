import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class TaskSerializer implements JsonSerializer<Task> {
    @Override
    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("id", task.getId());
        result.addProperty("name", task.getName());
        result.addProperty("description", task.getDescription());
        result.addProperty("status", task.getStatus().getValue());
        var d = task.getDuration();
        var s = task.getStartTime();
        if (d != null) {
            result.addProperty("duration", d.toMinutes());
        }
        if (s != null) {
            result.addProperty("startTime", s.toString());
        }
        return result;
    }
}
