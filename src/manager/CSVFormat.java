public class CSVFormat {

    private CSVFormat() {

    }

    public static Task fromString(String value) {
        var fields = value.split(",");
        Task result;
        Integer id = Integer.parseInt(fields[0]);
        Status st = Status.valueOf(fields[3]);
        String name = fields[2];
        String descr = fields[4];
        String type = fields[1];

        switch (type) {
            case "task":
                result = new Task(id, name, descr, st);
                break;
            case "subtask":
                int epicId = Integer.parseInt(fields[5]);
                result = new Subtask(id, epicId, name, descr, st);
                break;
            case "epic":
                result = new Epic(id, name, descr, st);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return result;
    }

    public static String getHeader() {
        return "id,type,name,status,description,startTime,duration,epic";
    }
}
