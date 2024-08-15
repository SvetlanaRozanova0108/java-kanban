public class ManagerSaveException extends RuntimeException {

    private static final String messageSave = "Error occurred while saving";
    private static final String messageLoad = "Error occurred while loading";

    public static ManagerSaveException saveException(Exception e) {
        return new ManagerSaveException(messageSave, e);
    }

    public static ManagerSaveException loadException(Exception e) {
        return new ManagerSaveException(messageLoad, e);
    }

    private ManagerSaveException(String message, Exception e) {
        super(message, e);
    }
}
