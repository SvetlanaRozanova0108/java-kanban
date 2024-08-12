import java.util.Comparator;

public class PrioritizedTasksComparator implements Comparator<Task> {
    @Override
    public int compare(Task item1, Task item2) {
        if (item1.getStartTime().isAfter(item2.getStartTime())) {
            return 1;

            // более дешёвый — ближе к началу списка
        } else if (item1.getStartTime().isBefore(item2.getStartTime())) {
            return -1;

            // если стоимость равна, нужно вернуть 0
        } else {
            return 0;
        }
    }
}
