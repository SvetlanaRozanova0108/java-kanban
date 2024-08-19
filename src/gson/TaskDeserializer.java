import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDateTime;

public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task result = null;
        var id = jsonObject.get("id");
        var name = jsonObject.get("name").getAsString();
        var status = Status.values()[jsonObject.get("status").getAsInt()];
        var description = jsonObject.get("description").getAsString();
        var durationMin = jsonObject.get("duration");
        var startTimeStr = jsonObject.get("startTime");

        if (id != null && !id.isJsonNull()) {
            result = new Task(id.getAsInt(), name, description, status);
        } else {
            result = new Task(name, description);
            result.setStatus(status);
        }

        if (startTimeStr != null && !startTimeStr.isJsonNull()) {
            result.setStartTime(LocalDateTime.parse(startTimeStr.getAsString()));
        }
        if (durationMin != null && !durationMin.isJsonNull()) {
            result.setDuration(Duration.ofMinutes(durationMin.getAsLong()));
        }
        return result;
    }
}
