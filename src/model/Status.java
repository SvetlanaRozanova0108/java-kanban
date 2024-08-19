public enum Status {
    NEW(0),
    IN_PROGRESS(10),
    DONE(50);

    private final int value;

    private Status(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
