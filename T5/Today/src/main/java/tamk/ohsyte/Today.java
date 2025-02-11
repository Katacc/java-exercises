package tamk.ohsyte;

import java.time.MonthDay;
import java.util.Collections;
import java.util.List;

public class Today {
    public static void main(String[] args) {
        // Replace with a valid path to the events.csv file on your own computer!

        String userprofile = System.getenv("USERPROFILE");

        final String fileName = userprofile + "/.today/events.csv";


        // Per the documentation of the class, I initialized the list before trying to read the file.
        List<Event> events = null;


        // Exception is handled in CSVEventProvider.java
        // No additional exception handling needed
        EventProvider provider = new CSVEventProvider(fileName);


        final MonthDay monthDay = MonthDay.of(2, 11);

        // Get events for given day, any year, any category, newest first
        events = provider.getEventsOfDate(monthDay);
        Collections.sort(events);
        Collections.reverse(events);

        for (Event event : events) {
            System.out.println(event);
        }
    }
}