import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class SubtaskSerializer implements JsonSerializer<Subtask> {
    @Override
    public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("id", subtask.getId());
        result.addProperty("epicId", subtask.getEpicID());
        result.addProperty("name", subtask.getName());
        result.addProperty("description", subtask.getDescription());
        result.addProperty("status", subtask.getStatus().getValue());
        var d = subtask.getDuration();
        var s = subtask.getStartTime();
        if (d != null) {
            result.addProperty("duration", d.toMinutes());
        }
        if (s != null) {
            result.addProperty("startTime", s.toString());
        }
        return result;
    }
}
