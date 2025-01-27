import java.time.LocalDate;

public class EventTest {
    public static void main(String[] args) {
        Category cat1 = new Category("apple", "macos");
        Category cat2 = new Category("apple", "ios");
        System.out.println(
            String.format("'%s' = '%s'? %b", cat1, cat2, cat1.equals(cat2)));
        System.out.println(
            String.format(
                "cat1 hashCode=%d\ncat2 hashCode=%d",
                cat1.hashCode(), cat2.hashCode()));

        Category test = new Category("test", "test");
        Category category = new Category("test", "test");
        System.out.println(
            String.format(
                "%d %d %b",
                test.hashCode(), category.hashCode(), test.equals(category)));

        Event[] events = new Event[args.length];
        for (int i = 0; i < args.length; i++) {
            Event event = new Event(LocalDate.now(), args[i], category);
            events[i] = event;
        }

        for (Event event : events) {
            System.out.println(event + " - hashCode=" + event.hashCode());
        }
    }
}