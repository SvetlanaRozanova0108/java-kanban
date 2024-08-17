import com.google.gson.*;

import java.lang.reflect.Type;

public class EpicDeserializer implements JsonDeserializer<Epic> {
    @Override
    public Epic deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic result = null;
        var id = jsonObject.get("id");
        var name = jsonObject.get("name").getAsString();
        var description = jsonObject.get("description").getAsString();
        var status = Status.values()[jsonObject.get("status").getAsInt()];
        if (id != null && !id.isJsonNull()) {
            result = new Epic(id.getAsInt(), name, description, status);
        } else {
            result = new Epic(null, name, description, status);
        }
        return result;
    }
}
